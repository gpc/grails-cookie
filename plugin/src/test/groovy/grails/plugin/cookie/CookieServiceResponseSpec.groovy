package grails.plugin.cookie

import grails.testing.services.ServiceUnitTest

class CookieServiceResponseSpec extends CookieResponseSpec implements ServiceUnitTest<CookieService> {

    @Override
    void setup() {
        obj = service
    }
}
