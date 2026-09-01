[![Maven Central](https://img.shields.io/maven-central/v/io.github.gpc/grails-cookie.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.gpc/grails-cookie)
[![License](https://img.shields.io/github/license/gpc/grails-cookie)](https://www.apache.org/licenses/LICENSE-2.0)
[![CI](https://github.com/gpc/grails-cookie/actions/workflows/ci.yml/badge.svg?event=push)](https://github.com/gpc/grails-cookie/actions/workflows/ci.yml)

Grails Cookie Plugin
====================

Makes dealing with cookies easy. The plugin provides an injectable `CookieService` and Groovy extension
methods on `HttpServletRequest` / `HttpServletResponse`, so getting, setting, and deleting a cookie is a
single line of code. It is [RFC 6265](https://tools.ietf.org/html/rfc6265) compliant.

The user guide can be found here: 📚 [Documentation]

## Installation

Add the following dependency to the `build.gradle` file:

### Grails 8.x

```groovy
dependencies {
    implementation("io.github.gpc:grails-cookie:4.0.0-SNAPSHOT")
}
```

### Grails 7.x

```groovy
dependencies {
    implementation("io.github.gpc:grails-cookie:3.0.0")
}
```

## Compatibility

| Plugin version | Grails version | Java | Groovy | Coordinate                          |
|----------------|----------------|------|--------|-------------------------------------|
| 4.x            | 8.0.x          | 21+  | 5.x    | `io.github.gpc:grails-cookie`       |
| 3.x            | 7.0.x          | 17+  | 4.x    | `io.github.gpc:grails-cookie`       |
| 2.x            | 3.0.x          | 7+   | 2.x    | `org.grails.plugins:cookie:2.0.5`   |
| 1.x            | 2.0.x          | 7+   | 2.x    | `org.grails.plugins:cookie:1.4.0`   |

## Building from Source

Tool versions are pinned in `.sdkmanrc`. With [SDKMAN](https://sdkman.io) installed:

```bash
sdk env install
```

```bash
./gradlew build
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

[Documentation]: https://gpc.github.io/grails-cookie/
