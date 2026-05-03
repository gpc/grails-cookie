# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A Grails 7 plugin (`io.github.gpc:grails-cookie`) that simplifies cookie handling in Grails applications. It provides RFC 6265-compliant cookie operations through two mechanisms: an injectable `CookieService` (Spring bean) and Groovy extension methods added to `HttpServletRequest` / `HttpServletResponse`.

## Project structure

Multi-module Gradle composite build. Modules are declared in `settings.gradle`; `build-logic/` is an included composite build holding 12 convention plugins.

```
plugin/       — the published JAR (source + unit tests only)
examples/app1/ — default-config integration tests
examples/app2/ — config-override integration tests
build-logic/   — 12 convention plugins (config.*.gradle)
code-coverage/ — JaCoCo aggregation
docs/          — Asciidoctor documentation
```

## Common commands

```bash
sdk env                                  # activate Java 17 / Gradle 8.14.4 from .sdkmanrc
./gradlew build                          # compile + all unit + integration tests
./gradlew :grails-cookie:test            # plugin unit tests only
./gradlew :grails-cookie:test --tests "grails.plugin.cookie.CookieHelperDefaultsSpec"
./gradlew :app1:integrationTest          # default-config integration tests
./gradlew :app2:integrationTest          # config-override integration tests
./gradlew publishToMavenLocal            # install to ~/.m2 for downstream testing
./gradlew :code-coverage:jacocoAggregatedReport
```

## Architecture

Three pieces cooperate to expose the same cookie API on `request`, `response`, and an injectable service:

1. **`CookieHelper`** (`plugin/src/main/groovy/grails/plugin/cookie/CookieHelper.groovy`)
   Single source of truth for cookie construction and config-driven defaults. Implements `ServletAttributes` + `WebAttributes`. Overrides `getGrailsApplication()` to try `GrailsWebRequest.lookup().applicationContext` first, then `Holders.grailsApplication` as fallback — this is deliberate so it works both as a non-bean class (static field in extension class) and in unit tests that bind a `GrailsWebRequest` with an application context.

2. **`RequestResponseCookieExtension`** (`plugin/src/main/groovy/grails/plugin/cookie/RequestResponseCookieExtension.groovy`)
   Groovy **extension module** (`@CompileStatic`) adding `getCookie`/`findCookie` to `HttpServletRequest` and `setCookie`/`deleteCookie` to `HttpServletResponse`. Registered via `plugin/src/main/resources/META-INF/services/org.codehaus.groovy.runtime.ExtensionModule` — if you add or rename methods here, update that descriptor too. Holds a single static `CookieHelper` instance.

3. **`CookieService`** (`plugin/grails-app/services/grails/plugin/cookie/CookieService.groovy`)
   Thin Spring service that delegates to the request/response extension methods. Exists for DI and mockability. Keep it a pass-through — logic belongs in `CookieHelper`.

## Configuration keys

All read in `CookieHelper` via `grailsApplication.config.getProperty(key, type, default)`:

| Key | Default | Notes |
|-----|---------|-------|
| `grails.plugins.cookie.cookieage.default` | 30 days (2592000 s) | |
| `grails.plugins.cookie.path.defaultStrategy` | `'context'` | Also: `'root'`, `'current'` |
| `grails.plugins.cookie.secure.default` | mirrors `request.secure` | |
| `grails.plugins.cookie.httpOnly.default` | `true` | |

Boolean config values are parsed via `.toString().toBoolean()` — this is deliberate (supports external `.properties` files sending `"true"`/`"false"` strings).

## Testing notes

Unit tests live in `plugin/src/test/groovy/` only. The abstract base classes (`CookieRequestSpec`, `CookieResponseSpec`) implement `GrailsUnitTest` and set up `GrailsWebRequest` with the test `applicationContext` — this is what allows `CookieHelper.getGrailsApplication()` to find `grailsApplication` during unit tests.

`CookieHelperDefaultsSpec` uses `GrailsWebUnitTest`. Config is manipulated per-feature via `grailsApplication.config.merge([...])`.

The `ServiceUnitTest<CookieService>` trait (in `CookieServiceRequestSpec` / `CookieServiceResponseSpec`) provides the `service` property. `GrailsUnitTest` (from the base class) provides `applicationContext` — both run because Spock calls all `setup()` methods in the hierarchy.

Integration tests live in `examples/app1/src/integration-test/` (default config) and `examples/app2/src/integration-test/` (config overrides). Each example app is a full Grails 7 web app that depends on `project(':grails-cookie')`.
