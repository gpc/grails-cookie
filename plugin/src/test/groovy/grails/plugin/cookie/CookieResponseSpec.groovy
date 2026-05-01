package grails.plugin.cookie

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

import spock.lang.Specification
import spock.lang.Unroll

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext

import org.grails.testing.GrailsUnitTest
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils

abstract class CookieResponseSpec extends Specification implements GrailsUnitTest {

    protected HttpServletResponse response = new MockHttpServletResponse()
    protected obj
    private MockHttpServletRequest request = new MockHttpServletRequest()

    void setup() {
        request.contextPath = '/ctx'
        def mockWebRequest = new GrailsWebRequest(request, response, new MockServletContext(), applicationContext)
        WebUtils.storeGrailsWebRequest(mockWebRequest)
    }

    def cleanup() {
        WebUtils.clearGrailsWebRequest()
    }

    @Unroll
    void "setCookie() with args as list"() {
        given:
        obj.setCookie(args)

        expect:
        verifyAll(response.cookies[0]) {
            name == 'cookie_name'
            value == 'cookie_val'
            maxAge == aMaxAge
            path == aPath
            domain == aDomain
            secure == isSecure
            httpOnly == isHttpOnly
        }

        where:
        args                                                                     | aMaxAge | aPath    | aDomain        | isSecure | isHttpOnly
        ['cookie_name', 'cookie_val']                                            | 2592000 | '/ctx'   | null           | false    | true
        ['cookie_name', 'cookie_val', 42]                                        | 42      | '/ctx'   | null           | false    | true
        ['cookie_name', 'cookie_val', 42, '/aPath']                              | 42      | '/aPath' | null           | false    | true
        ['cookie_name', 'cookie_val', 42, '/aPath', '.example.com', true, false] | 42      | '/aPath' | '.example.com' | true     | false
    }

    @Unroll
    void "setCookie()"() {
        given:
        obj.setCookie('cookie_name', 'cookie_val', aMaxAge, aPath, aDomain, isSecure, isHttpOnly)

        expect:
        verifyAll(response.cookies[0]) {
            name == 'cookie_name'
            value == 'cookie_val'
            maxAge == anExpectedMaxAge
            path == anExpectedPath
            domain == aDomain
            secure == isSecure
            httpOnly == isHttpOnly
        }

        where:
        aMaxAge | anExpectedMaxAge | aPath    | anExpectedPath | aDomain        | isSecure | isHttpOnly
        null    | 2592000          | '/ctx'   | '/ctx'         | null           | false    | false
        42      | 42               | '/ctx'   | '/ctx'         | null           | false    | false
        42      | 42               | '/aPath' | '/aPath'       | null           | false    | false
        42      | 42               | '/aPath' | '/aPath'       | '.example.com' | true     | true
    }

    @Unroll
    void "setCookie() named params"() {
        given:
        def cookieThatWasSet = obj.setCookie(args)

        expect:
        verifyAll(response.cookies[0]) {
            name == 'cookie_name'
            value == 'cookie_val'
            maxAge == aMaxAge
            path == aPath
            domain == aDomain
            secure == isSecure
            httpOnly == isHttpOnly
        }

        where:
        args                                                                                                                          | aMaxAge | aPath    | aDomain        | isSecure | isHttpOnly
        [name: 'cookie_name', value: 'cookie_val']                                                                                    | 2592000 | '/ctx'   | null           | false    | true
        [name: 'cookie_name', value: 'cookie_val', maxAge: 42]                                                                        | 42      | '/ctx'   | null           | false    | true
        [name: 'cookie_name', value: 'cookie_val', maxAge: 42, path: '/aPath']                                                        | 42      | '/aPath' | null           | false    | true
        [name: 'cookie_name', value: 'cookie_val', maxAge: 42, path: '/aPath', domain: '.example.com', secure: true, httpOnly: false] | 42      | '/aPath' | '.example.com' | true     | false
    }

    @Unroll
    void "setCookie(Cookie) doesn't set defaults"() {
        given:
        Cookie cookie = new Cookie('cookie_name', 'some_val').tap {
            path = aPath
            maxAge = aMaxAge
            if (aDomain) {
                domain = aDomain
            }
            secure = isSecure
            httpOnly = isHttpOnly
        }
        obj.setCookie(cookie)

        expect:
        verifyAll(response.cookies[0]) {
            name == 'cookie_name'
            value == 'some_val'
            maxAge == aMaxAge
            path == aPath
            domain == aDomain
            secure == isSecure
            httpOnly == isHttpOnly
        }

        where:
        aMaxAge | aPath    | aDomain        | isSecure | isHttpOnly
        -1      | null     | null           | false    | false
        42      | '/'      | null           | false    | false
        42      | '/aPath' | null           | false    | false
        42      | '/aPath' | '.example.com' | true     | true
    }

    @Unroll
    def "deleteCookie() with args as list, sets new cookie with same name but expired age"() {
        given:
        def cookieThatWasSet = obj.deleteCookie(args)
        
        expect:
        verifyAll(cookieThatWasSet) {
            it == response.cookies[0]
            name == 'cookie_name'
            value == null
            path == aPath
            domain == aDomain
            maxAge == 0
        }
        
        where:
        args                                      | aPath | aDomain
        ['cookie_name']                           | '/ctx'   | null
        ['cookie_name', '/aPath']                 | '/aPath' | null
        ['cookie_name', '/aPath', '.example.com'] | '/aPath' | '.example.com'
    }

    @Unroll
    def "deleteCookie() sets new cookie with same name but expired age: #path #domain"() {
        given:
        def cookieThatWasSet = obj.deleteCookie('cookie_name', path, domain)
        expect:
        cookieThatWasSet == response.cookies[0]
        cookieThatWasSet.name == 'cookie_name'
        cookieThatWasSet.value == null
        cookieThatWasSet.path == pathExpected
        cookieThatWasSet.domain == domain
        cookieThatWasSet.maxAge == 0
        where:
        path     | pathExpected | domain
        null     | '/ctx'       | null
        '/aPath' | '/aPath'     | null
        '/aPath' | '/aPath'     | '.example.com'
    }

    @Unroll
    def "deleteCookie(Cookie) sets new cookie with same name but expired age: #path #pathExpected #domain"() {
        given:
        Cookie cookieToDelete = new Cookie('cookie_name', 'some_val')
        cookieToDelete.path = path
        if (domain) {
            cookieToDelete.domain = domain
        }
        def cookieThatWasSet = obj.deleteCookie(cookieToDelete)
        expect:
        cookieThatWasSet == response.cookies[0]
        cookieThatWasSet.name == 'cookie_name'
        cookieThatWasSet.value == null
        cookieThatWasSet.path == pathExpected
        cookieThatWasSet.domain == domain
        cookieThatWasSet.maxAge == 0
        where:
        path     | pathExpected | domain
        null     | '/ctx'       | null
        '/'      | '/'          | null
        '/aPath' | '/aPath'     | null
        '/aPath' | '/aPath'     | '.example.com'
    }
}
