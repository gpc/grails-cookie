# AGENTS.md — grails-cookie

## Project Overview

A Grails plugin (`io.github.gpc:grails-cookie`) that simplifies cookie handling in Grails applications.
It provides RFC 6265-compliant cookie operations through two mechanisms: an injectable `CookieService`
(Spring bean) and Groovy extension methods added to `HttpServletRequest` / `HttpServletResponse`.

- **Language:** Groovy 5.1.0 on Java 21
- **Framework:** Grails 8.0.0-M6
- **Build System:** Gradle 9.6.0 (with wrapper)
- **Current Version:** 4.0.0-SNAPSHOT
- **License:** Apache 2.0

This repository is built on
[grails-plugin-template](https://github.com/grails-plugins/grails-plugin-template) and receives
infrastructure updates through its automated file sync.

## Skill Files (Best Practices)

Detailed best practices are documented as skills in `.agents/skills/` (`.claude` is a symlink to `.agents`):

| Skill                                                                                  | Purpose                                                 |
|----------------------------------------------------------------------------------------|---------------------------------------------------------|
| [`repository-structure`](.agents/skills/repository-structure/SKILL.md)                 | Canonical directory layout and architectural rules      |
| [`gradle-best-practices`](.agents/skills/gradle-best-practices/SKILL.md)               | Gradle best practices, convention plugins, and idioms   |
| [`plugin-project`](.agents/skills/plugin-project/SKILL.md)                             | Plugin project scope: source code + unit tests only     |
| [`example-apps`](.agents/skills/example-apps/SKILL.md)                                 | Example app patterns: integration & functional tests    |
| [`enhance-plugin-with-template`](.agents/skills/enhance-plugin-with-template/SKILL.md) | Migrate an existing plugin onto the template structure  |

**Read these skill files before making structural changes to the repository.**

## Critical Rules

1. **NEVER add code to the root `build.gradle` to configure subprojects.** No `subprojects {}`,
   `allprojects {}`, or `configure()` blocks. All shared configuration goes through convention plugins in
   `build-logic/`.
2. **The plugin project contains ONLY plugin code and unit tests.** No integration tests, no functional
   tests, no example controllers or views.
3. **Example apps under `examples/` host all integration and functional tests.** They depend on the plugin
   via `implementation project(':grails-cookie')` and test it as a real consumer would.
4. **Use Gradle convention plugins to deduplicate.** If two or more subprojects share build logic, extract
   it into a convention plugin in `build-logic/`.
5. **Always use lazy Gradle APIs** to avoid eager initialization (`tasks.register()`, `tasks.named()`,
   `configureEach`, `provider {}`).
6. **Do not add business logic to `CookieService`** — it is intentionally a pass-through to the extension
   methods. Logic belongs in `CookieHelper`.

## Repository Structure

```
grails-cookie/
├── .agents/skills/            # Agent skill files (.claude is a symlink to .agents)
├── plugin/                    # The published JAR (artifact: grails-cookie)
│   ├── grails-app/            #   CookieService + plugin application.yml
│   └── src/                   #   Plugin source code and unit tests
├── examples/
│   ├── default-config/        # Integration tests against the plugin's defaults
│   └── config-override/       # Integration tests against overridden config
├── docs/                      # Asciidoctor documentation
├── build-logic/               # Gradle convention plugins (composite build)
├── code-coverage/             # JaCoCo aggregation
├── .github/workflows/         # CI, release, release-notes, contributors, versions
├── build.gradle               # Root build file (docs + root-publish ONLY)
├── settings.gradle            # Multi-project settings
├── gradle.properties          # Version properties
└── project.yml                # Plugin metadata (POM, docs, release notes)
```

## Build and Test Commands

```bash
# Full build (compile + all unit and integration tests)
./gradlew build

# Run only unit tests (plugin module)
./gradlew :grails-cookie:test

# Run a single unit test class
./gradlew :grails-cookie:test --tests "grails.plugin.cookie.CookieHelperDefaultsSpec"

# Run integration tests
./gradlew :default-config:integrationTest
./gradlew :config-override:integrationTest

# Skip tests
./gradlew build -PskipTests

# Run an example app
./gradlew :default-config:bootRun

# Generate documentation
./gradlew docs

# Run code style checks only / skip them
./gradlew codeStyle
./gradlew build -PskipCodeStyle

# Aggregated coverage report
./gradlew :code-coverage:jacocoAggregatedReport

# Install to ~/.m2 for downstream testing
./gradlew publishToMavenLocal

# Verify the repository still conforms to the template
groovy .github/scripts/verify-repository.groovy
```

## SDK Requirements

Use SDKMAN to install the correct tool versions (see `.sdkmanrc`):

- Java: `21.0.12-librca`
- Gradle: `9.6.0`
- Groovy: `5.1.0`

Run `sdk env install` to set up the environment.

NOTE: Gradle **9.7.0 does not work** with the Grails 8.0.0-M6 Gradle plugin — `compileGroovy` fails with
`groovyOptions.configurationScriptFile is final and cannot be changed any further`. Stay on 9.6.0 until a
later Grails 8 milestone fixes it.

## Architecture

Three pieces cooperate to expose the same cookie API on `request`, `response`, and an injectable service:

1. **`CookieHelper`** (`plugin/src/main/groovy/grails/plugin/cookie/CookieHelper.groovy`)
   Single source of truth for cookie construction and config-driven defaults. Implements
   `ServletAttributes` + `WebAttributes`. Overrides `getGrailsApplication()` to try
   `GrailsWebRequest.lookup().applicationContext` first, then `Holders.grailsApplication` as fallback —
   this is deliberate so it works both as a non-bean class (static field in the extension class) and in
   unit tests that bind a `GrailsWebRequest` with an application context. **Do not break this chain.**

2. **`RequestResponseCookieExtension`**
   (`plugin/src/main/groovy/grails/plugin/cookie/RequestResponseCookieExtension.groovy`)
   Groovy **extension module** (`@CompileStatic`) adding `getCookie`/`findCookie` to `HttpServletRequest`
   and `setCookie`/`deleteCookie` to `HttpServletResponse`. Registered via
   `plugin/src/main/resources/META-INF/services/org.codehaus.groovy.runtime.ExtensionModule` — if you add
   or rename methods here, update that descriptor too (including its `moduleVersion`).

3. **`CookieService`** (`plugin/grails-app/services/grails/plugin/cookie/CookieService.groovy`)
   Thin Spring service that delegates to the request/response extension methods. Exists for DI and
   mockability. Keep it a pass-through.

### Core Classes

| Class                           | Location                                          | Purpose                                              |
|---------------------------------|---------------------------------------------------|------------------------------------------------------|
| `CookieGrailsPlugin`            | `plugin/src/main/groovy/grails/plugin/cookie/`    | Plugin descriptor                                    |
| `CookieHelper`                  | `plugin/src/main/groovy/grails/plugin/cookie/`    | Default resolution (maxAge, path, secure, httpOnly)  |
| `RequestResponseCookieExtension`| `plugin/src/main/groovy/grails/plugin/cookie/`    | Groovy extension module for request/response         |
| `CookieService`                 | `plugin/grails-app/services/grails/plugin/cookie/`| Thin injectable service                              |

## Configuration

All keys are optional and read in `CookieHelper` via
`grailsApplication.config.getProperty(key, type, default)`:

| Key                                        | Default                     | Notes                          |
|--------------------------------------------|-----------------------------|--------------------------------|
| `grails.plugins.cookie.cookieage.default`  | 30 days (2 592 000 s)       |                                |
| `grails.plugins.cookie.path.defaultStrategy` | `'context'`               | Also: `'root'`, `'current'`    |
| `grails.plugins.cookie.secure.default`     | mirrors `request.secure`    | Boolean or string              |
| `grails.plugins.cookie.httpOnly.default`   | `true`                      | Boolean or string              |

Boolean config values are parsed via `.toString().toBoolean()` — this is deliberate, so external
`.properties` files that supply `"true"`/`"false"` strings work as expected.

## Testing

### Unit Tests (`plugin/src/test/groovy/`)

Unit tests use the **Spock Framework** and run on the JUnit Platform. They live in the plugin project only.

The abstract base classes (`CookieRequestSpec`, `CookieResponseSpec`) implement `GrailsUnitTest` and set up
a `GrailsWebRequest` with the test `applicationContext` — this is what allows
`CookieHelper.getGrailsApplication()` to resolve `grailsApplication` during unit tests.

`CookieHelperDefaultsSpec` uses `GrailsWebUnitTest`; config is manipulated per-feature via
`grailsApplication.config.merge([...])`.

The `ServiceUnitTest<CookieService>` trait (in `CookieServiceRequestSpec` / `CookieServiceResponseSpec`)
provides the `service` property, while `GrailsUnitTest` (from the base class) provides `applicationContext`
— both run because Spock calls all `setup()` methods in the hierarchy.

### Integration Tests (`examples/*/src/integration-test/`)

Each example app is a full Grails 8 web app depending on `project(':grails-cookie')`. They boot the app and
assert on real HTTP response headers.

- `examples/default-config/` — the plugin's out-of-the-box defaults
- `examples/config-override/` — the same controller with `grails.plugins.cookie.*` overrides in
  `application.yml`

### Behaviour notes

- **`cookie.version` is not set** — it was removed since Servlet 6 (`jakarta.servlet`) deprecated
  `Cookie.setVersion()`. Tests should not assert on `cookie.version`.
- **A deleted cookie no longer emits `Max-Age=0`.** Tomcat 11 (Servlet 6.1) serialises a zero `Max-Age` as
  `Expires=Thu, 01 Jan 1970 00:00:10 GMT` instead. Assert on either form.

## Build-Logic Convention Plugins

Convention plugins in `build-logic/src/main/groovy/` standardize build configuration:

| Plugin                            | Purpose                                                                              |
|-----------------------------------|--------------------------------------------------------------------------------------|
| `config.app-run.gradle`           | Debug flags for `bootRun`                                                            |
| `config.code-coverage.gradle`     | JaCoCo per project                                                                   |
| `config.code-coverage-aggregate.gradle` | Aggregated JaCoCo report                                                       |
| `config.code-style.gradle`        | Checkstyle + CodeNarc                                                                |
| `config.compile.gradle`           | Java/Groovy compilation settings (UTF-8, incremental, Java release from `.sdkmanrc`) |
| `config.docs.gradle`              | Documentation aggregation (Groovydoc + Asciidoctor)                                  |
| `config.example-app.gradle`       | Example app config (grails-web, GSP, assets)                                         |
| `config.grails-assets.gradle`     | Asset pipeline with Bootstrap/jQuery WebJars                                         |
| `config.grails-plugin.gradle`     | Grails plugin application                                                            |
| `config.grails-web-plugin.gradle` | `config.grails-plugin` + asset-pipeline packaging (for plugins shipping assets)      |
| `config.project-metadata.gradle`  | Exposes `project.yml` values as Gradle extra properties                               |
| `config.publish.gradle`           | Per-project Maven publishing metadata (driven by `project.yml`)                      |
| `config.publish-root.gradle`      | Root-level Nexus publishing workaround                                               |
| `config.testing.gradle`           | Test framework config (Spock, JUnit Platform, test-logger)                           |

`build-logic/` is normally managed by the template's file sync and must not be hand-edited. Three files here
had to diverge for Grails 8 and are therefore locked with a sibling `.lock` file — see the comment inside
each for the reason and the condition for removing the lock:

- `config.code-coverage.gradle` — declares `jacocoAgent`/`jacocoAnt` explicitly
- `config.compile.gradle` — carries the Groovydoc conventions
- `config.docs.gradle` — `aggregateGroovyApiDoc` collects the plugin project's own Groovydoc output

`.sdkmanrc`, `gradlew`, and `gradlew.bat` are locked for the same reason (Java 21 / Gradle 9.6.0).
`gradle/wrapper/` is **not** lock-aware in the template's `files-sync.yml`, so a sync PR will try to
downgrade the wrapper jar and `distributionUrl` — reject that part until the template targets Grails 8.

### Template bug fixed here: `update-index` job permissions

`ci.yml` and `release.yml` both call `update-versions.yml`, which declares
`permissions: pull-requests: write` (its `commit-or-pr.sh` opens a PR when a direct push to a
protected branch is rejected). A called workflow may not request more than the calling job grants, so
the template's `permissions: contents: write` on those `update-index` jobs makes the **entire run**
fail at startup with:

```
The workflow is requesting 'pull-requests: write', but is only allowed 'pull-requests: none'.
```

Both callers now also grant `pull-requests: write`. This never surfaces in the template repo because
its `update-index` job is guarded off there. These two files are deliberately **not** locked, so
future CI improvements still reach this repo — but a template sync that reverts the fix will make CI
fail at startup on the sync PR itself. If that happens, re-apply the two `pull-requests: write`
lines rather than merging the revert, and fix it upstream in the template.

## CI/CD

- **CI** (`.github/workflows/ci.yml`): verifies repository structure, builds and tests on push/PR, then
  publishes snapshots to Maven Central Snapshots and docs to GitHub Pages on push to release branches.
- **Release** (`.github/workflows/release.yml`): multi-stage pipeline triggered by a GitHub release — stage
  artifacts, release to Maven Central, publish docs to GitHub Pages, bump version.
- **Release Notes** (`.github/workflows/release-notes.yml`): auto-drafts release notes via release-drafter.
- **Contributors / Versions** (`update-contributors.yml`, `update-versions.yml`): write the `contributors`
  and `versions` blocks of `project.yml` back to the default branch.

## Code Conventions

- Groovy source files use standard Grails conventions (services and taglibs in `grails-app/`, other classes
  in `src/main/groovy/`).
- **Use `def` for local variables** where the type is inferred from the right-hand side (constructor calls,
  method calls, casts, factory methods). Explicit types only where the type cannot be inferred or where
  `@CompileStatic` needs them. This applies to both production code and tests.
- When writing Gradle, always use the latest best practices to avoid eager initialization.
