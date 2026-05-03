package grails.plugin.cookie

import grails.testing.services.ServiceUnitTest

class CookieServiceRequestSpec extends CookieRequestSpec implements ServiceUnitTest<CookieService> {

    @Override
    def setup() {
        obj = service
    }
}
