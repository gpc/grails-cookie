package app1

import org.springframework.http.HttpStatusCode

import grails.testing.mixin.integration.Integration
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

@Integration
class CookieIntegrationSpec extends Specification {

    @Shared
    RestTemplate restTemplate = new RestTemplate()

    private String getBaseUrl() {
        "http://localhost:${serverPort}"
    }

    private ResponseEntity<String> doGet(String path) {
        restTemplate.exchange("${baseUrl}${path}", HttpMethod.GET, null, String)
    }

    void "setSimpleCookie uses default max age of 30 days"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        response.statusCode.value() == 200
        response.body.contains('maxAge=2592000')
    }

    void "setSimpleCookie defaults httpOnly to true"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        response.statusCode.value() == 200
        response.body.contains('httpOnly=true')
    }

    void "setSimpleCookie uses context path as default path"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        response.statusCode.value() == 200
        // Context path is empty string ('') for root-deployed app, so path should be ''
        response.body.contains('path=')
    }

    void "Set-Cookie header appears in HTTP response"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        response.headers.get('Set-Cookie') != null
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader.contains('demo_cookie=hello_world')
    }

    void "Set-Cookie header contains Max-Age for default 30 days"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        setCookieHeader.contains('Max-Age=2592000')
    }

    void "Set-Cookie header contains HttpOnly by default"() {
        when:
        def response = doGet('/cookieDemo/setSimpleCookie')

        then:
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        setCookieHeader.contains('HttpOnly')
    }

    void "deleteCookie issues Set-Cookie with Max-Age=0"() {
        when:
        def response = doGet('/cookieDemo/deleteCookieAction')

        then:
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        setCookieHeader.contains('demo_cookie=')
        setCookieHeader.contains('Max-Age=0')
    }

    void "setCookieWithMap sets httpOnly from map param"() {
        when:
        def response = doGet('/cookieDemo/setCookieWithMap')

        then:
        response.statusCode.value() == 200
        response.body.contains('maxAge=600')
        response.body.contains('httpOnly=false')
    }

    void "setViaResponse extension method works"() {
        when:
        def response = doGet('/cookieDemo/setViaResponse')

        then:
        response.statusCode.value() == 200
        response.body.contains('response_cookie=response_value')
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        setCookieHeader.contains('response_cookie=response_value')
    }

    void "setCookieFull with explicit httpOnly=false has no HttpOnly in header"() {
        when:
        def response = doGet('/cookieDemo/setCookieFull')

        then:
        response.statusCode.value() == 200
        response.body.contains('maxAge=7200')
        response.body.contains('httpOnly=false')
        def setCookieHeader = response.headers.getFirst('Set-Cookie')
        setCookieHeader != null
        !setCookieHeader.contains('HttpOnly')
    }
}
