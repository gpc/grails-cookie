package grails.plugin.cookie

import spock.lang.Specification

class CookieServiceMockSpec extends Specification {

    CookieService cookieService = Mock(CookieService)

    def "Ensure mocking the service works"() {
        when:
        def cookie = cookieService.findCookie('mock')

        then:
        !cookie
    }
}
