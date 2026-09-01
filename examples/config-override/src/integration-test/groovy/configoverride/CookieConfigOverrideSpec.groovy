package configoverride

import spock.lang.Shared
import spock.lang.Specification

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

import grails.testing.mixin.integration.Integration

/**
 * Verifies that the cookie plugin respects config overrides from application.yml:
 *   grails.plugins.cookie.cookieage.default = 3600
 *   grails.plugins.cookie.path.defaultStrategy = root
 *   grails.plugins.cookie.httpOnly.default = false
 *   grails.plugins.cookie.secure.default = false
 */
@Integration
class CookieConfigOverrideSpec extends Specification {

    @Shared
    RestTemplate restTemplate = new RestTemplate()

    private String getBaseUrl() {
        "http://localhost:${serverPort}"
    }

    private ResponseEntity<String> doGet(String path) {
        restTemplate.exchange("${baseUrl}${path}", HttpMethod.GET, null, String)
    }

    void "default max age comes from config override (3600s not 30 days)"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        response.statusCode.value() == 200
        response.body.contains('maxAge=3600')
        !response.body.contains('maxAge=2592000')
    }

    void "Set-Cookie header contains Max-Age=3600 from config override"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        setCookieHeader.contains('Max-Age=3600')
        !setCookieHeader.contains('Max-Age=2592000')
    }

    void "default path uses root strategy (/) from config override"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        response.statusCode.value() == 200
        response.body.contains('path=/')
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        setCookieHeader.contains('Path=/')
    }

    void "default httpOnly is false from config override — HttpOnly NOT in header"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        response.statusCode.value() == 200
        response.body.contains('httpOnly=false')
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        !setCookieHeader.contains('HttpOnly')
    }

    void "setViaResponse also respects config overrides"() {
        when:
        def response = doGet('/cookieDemo/setViaResponse')

        then:
        response.statusCode.value() == 200
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        setCookieHeader.contains('Max-Age=3600')
        setCookieHeader.contains('Path=/')
        !setCookieHeader.contains('HttpOnly')
    }
}
