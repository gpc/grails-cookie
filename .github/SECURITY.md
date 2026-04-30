# Security Policy

## Reporting a Vulnerability

If you discover a security vulnerability in this plugin, **please do not open a public GitHub issue**.

Instead, please report it responsibly by emailing the maintainers directly or
using [GitHub's private vulnerability reporting](https://github.com/gpc/grails-cookie/security/advisories/new).

### What to Include

- A description of the vulnerability
- Steps to reproduce the issue
- The potential impact
- Any suggested fixes (if you have them)

### Response Timeline

- We will acknowledge receipt within **48 hours**
- We will provide an initial assessment within **1 week**
- We will work with you to understand and resolve the issue before any public disclosure

## Security Considerations

Cookies are a fundamental part of web session management and carry inherent security risks. When using this plugin:

- Always set `httpOnly: true` (the default) to prevent JavaScript access and reduce XSS cookie theft risk
- Set `secure: true` for session cookies on HTTPS sites to prevent transmission over HTTP
- Use the `SameSite` attribute at the container level to mitigate CSRF risks
- Avoid storing sensitive data (session tokens, personal data) in cookie values without encryption
- The `grails.plugins.cookie.secure.default` config option mirrors `request.secure` by default — ensure your reverse proxy sets the `X-Forwarded-Proto` header correctly

## Supported Versions

| Version      | Grails    | Supported |
|--------------|-----------|-----------|
| 3.x (latest) | Grails 7  | Yes       |
| 2.x          | Grails 3  | No        |
