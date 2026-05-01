# Grails Cookie Plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.gpc/grails-cookie.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.gpc/grails-cookie)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![CI](https://github.com/gpc/grails-cookie/actions/workflows/ci.yml/badge.svg)](https://github.com/gpc/grails-cookie/actions/workflows/ci.yml)

This plugin makes dealing with cookies easy. It provides an injectable `CookieService` and Groovy extension methods on
`HttpServletRequest` / `HttpServletResponse` to get, set, and delete cookies with one line.

It is [RFC 6265](https://tools.ietf.org/html/rfc6265) compliant.

## Quick Start

Add to `build.gradle`:

```groovy
implementation 'io.github.gpc:grails-cookie:3.0.0'
```

### Snapshot builds

Snapshot versions are published to Maven Central Snapshots. To use a snapshot, add the repository:

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        maven { url = 'https://central.sonatype.com/repository/maven-snapshots/' }
        mavenCentral()
    }
}
```

Then add the dependency:

```groovy
implementation 'io.github.gpc:grails-cookie:3.0.0-SNAPSHOT'
```

## Usage

Two equivalent APIs are available everywhere in a Grails application:

### Extension methods on `request` / `response`

```groovy
// Set a cookie (default age = 30 days, HttpOnly = true, path = context path)
response.setCookie('username', 'cookieUser123')

// Set with explicit age (seconds), path, domain, secure, httpOnly
response.setCookie('username', 'cookieUser123', 604800, '/', null, false, true)

// Set via named params
response.setCookie([name: 'username', value: 'cookieUser123', maxAge: 604800, secure: true])

// Get the value
String value = request.getCookie('username')          // returns 'cookieUser123' or null

// Find the full Cookie object
Cookie cookie = request.findCookie('username')

// Delete (sets Max-Age=0 on a new cookie with the same name/path/domain)
response.deleteCookie('username')
response.deleteCookie('username', '/path', '.example.com')
response.deleteCookie(existingCookieObject)
```

### Injectable `CookieService`

```groovy
class MyService {
    CookieService cookieService

    void doSomething() {
        cookieService.setCookie('username', 'cookieUser123', 604800)
        String value = cookieService.getCookie('username')
        cookieService.deleteCookie('username')
    }
}
```

`CookieService` has the same method signatures as the response/request extension methods.

## Configuration

All config keys are optional. Defaults are intentionally safe for most applications.

### `grails.plugins.cookie.cookieage.default`

Default `Max-Age` for cookies in seconds.

- `null` or unset → **30 days** (2 592 000 s)
- `-1` → session cookie (removed when browser closes)
- `0` is reserved for deletion — do not use as a default

```yaml
grails:
    plugins:
        cookie:
            cookieage:
                default: 86400   # 1 day
```

### `grails.plugins.cookie.path.defaultStrategy`

How the cookie `Path` attribute is determined when none is supplied explicitly.

| Value     | Behaviour                         |
|-----------|-----------------------------------|
| `context` | Web app context path (default)    |
| `root`    | `/`                               |
| `current` | No path set (controller-relative) |

```yaml
grails:
    plugins:
        cookie:
            path:
                defaultStrategy: root
```

### `grails.plugins.cookie.secure.default`

Default `Secure` flag. If `null` or unset, mirrors `request.isSecure()` (i.e. cookies are secure when the connection is
HTTPS).

Boolean values 

```yaml
grails:
    plugins:
        cookie:
            secure:
                default: true
```

### `grails.plugins.cookie.httpOnly.default`

Default `HttpOnly` flag. Defaults to `true`. Accepts boolean and string boolean values.

```yaml
grails:
    plugins:
        cookie:
            httpOnly:
                default: false
```

## Compatibility

| Plugin version | Grails version | Java | Groovy | Coordinate                             |
|----------------|----------------|------|--------|----------------------------------------|
| 3.x            | 7.0.x          | 17+  | 4.0.x  | org.grails.plugins:grails-cookie:3.0.0 |
| 2.x            | 3.0.x          | 7+   | 2.x    | org.grails.plugins:cookie:2.0.5        |                     
| 1.x            | 2.0.x          | 7+   | 2.x    | org.grails.plugins:cookie:1.4.0        |                     

## Building from Source

Requirements: Java 17, SDKMAN (recommended).

```bash
sdk env           # activates Java / Gradle / Groovy from .sdkmanrc
./gradlew build   # compiles plugin, runs all unit + integration tests
```

Run only plugin unit tests:

```bash
./gradlew :grails-cookie:test
```

Run integration tests (boots example app):

```bash
./gradlew :app1:integrationTest   # default-config scenarios
./gradlew :app2:integrationTest   # config-override scenarios
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).
