package grails.plugin.cookie

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest

import spock.lang.Specification
import spock.lang.Unroll

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockServletContext

import org.grails.testing.GrailsUnitTest
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils

abstract class CookieRequestSpec extends Specification implements GrailsUnitTest {

    protected HttpServletRequest request = new MockHttpServletRequest()
    protected obj

    def setup() {
        def mockWebRequest = new GrailsWebRequest(request, new MockHttpServletResponse(), new MockServletContext(), applicationContext)
        WebUtils.storeGrailsWebRequest(mockWebRequest)
    }

    def cleanup() {
        WebUtils.clearGrailsWebRequest()
    }

    @Unroll
    def "findCookie() return cookie and is case-sensitive by name: #name #expectedCookieName"() {
        given:
        def cookie = new Cookie('cookie_name', 'cookie_val')
        request.cookies = [cookie]
        expect:
        obj.findCookie(name)?.name == expectedCookieName
        where:
        name          | expectedCookieName
        'cookie_name' | 'cookie_name'
        'CoOkIe_NaMe' | null
    }

    @Unroll
    def "getCookie() return cookie value and is case-sensitive by name: #name #expectedValue"() {
        given:
        request.cookies = [new Cookie('cookie_name', 'cookie_val')]
        expect:
        obj.getCookie(name) == expectedValue
        where:
        name          | expectedValue
        'cookie_name' | 'cookie_val'
        'CoOkIe_NaMe' | null
    }
}
