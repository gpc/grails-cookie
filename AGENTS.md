# AI Agent Notes — grails-cookie

## Repository purpose

`grails-cookie` is a Grails 7 plugin (`io.github.gpc:grails-cookie`) providing RFC 6265-compliant cookie operations via an injectable `CookieService` and Groovy extension methods on `HttpServletRequest` / `HttpServletResponse`. See [CLAUDE.md](CLAUDE.md) for detailed architecture and command reference.

## Architecture overview

### Core classes (`plugin/src/main/groovy/grails/plugin/cookie/`)

| Class | Role |
|-------|------|
| `CookieGrailsPlugin` | Plugin descriptor |
| `CookieHelper` | Default resolution logic (maxAge, path, secure, httpOnly) from `grailsApplication.config` |
| `RequestResponseCookieExtension` | Groovy extension module adding cookie methods to request/response |

### Service (`plugin/grails-app/services/grails/plugin/cookie/`)

| Class | Role |
|-------|------|
| `CookieService` | Thin injectable service; delegates to extension methods |

### Configuration keys (all optional)

| Key | Default | Notes |
|-----|---------|-------|
| `grails.plugins.cookie.cookieage.default` | 30 days (2 592 000 s) | |
| `grails.plugins.cookie.path.defaultStrategy` | `'context'` | Also: `'root'`, `'current'` |
| `grails.plugins.cookie.secure.default` | mirrors `request.secure` | Boolean or string |
| `grails.plugins.cookie.httpOnly.default` | `true` | Boolean or string |

### Tests

- **Unit tests** live in `plugin/src/test/groovy/` — they use Spring `MockHttpServlet*` + `GrailsWebRequest` (with `applicationContext`) to satisfy `CookieHelper.getGrailsApplication()`.
- **Integration tests** live in `examples/app1/src/integration-test/` (default config) and `examples/app2/src/integration-test/` (config overrides). They boot a full Grails app and assert on HTTP response headers.

## Key constraints

- **Do not add business logic to `CookieService`** — it is intentionally a pass-through to the extension methods.
- **The `CookieHelper` static instance in `RequestResponseCookieExtension` is not a Spring bean.** It resolves `grailsApplication` via `GrailsWebRequest.lookup().applicationContext` with `Holders.grailsApplication` as fallback. Do not break this chain.
- **ExtensionModule descriptor path** (`META-INF/services/org.codehaus.groovy.runtime.ExtensionModule`) — if you add or rename extension methods, update the descriptor too.
- **Config boolean parsing** uses `.toString().toBoolean()` — this is a deliberate fix to support external `.properties` files that supply `"true"`/`"false"` strings rather than booleans.
- **`cookie.version` is not set** — it was removed since Servlet 6 (`jakarta.servlet`) deprecated `Cookie.setVersion()`. Tests should not assert on `cookie.version`.
