package grails.plugin.cookie.demo

import jakarta.servlet.http.Cookie

import grails.plugin.cookie.CookieService

class CookieDemoController {

    CookieService cookieService

    def setSimpleCookie() {
        def cookie = cookieService.setCookie('demo_cookie', 'hello_world')
        render "Set cookie: ${cookie.name}=${cookie.value}, maxAge=${cookie.maxAge}, path=${cookie.path}, httpOnly=${cookie.httpOnly}, secure=${cookie.secure}"
    }

    def getCookieValue() {
        def value = cookieService.getCookie('demo_cookie')
        render "Got cookie: demo_cookie=${value}"
    }

    def deleteCookieAction() {
        cookieService.deleteCookie('demo_cookie')
        render 'Deleted cookie: demo_cookie'
    }

    def setCookieWithMap() {
        def cookie = cookieService.setCookie([name: 'map_cookie', value: 'map_value', maxAge: 600, httpOnly: false])
        render "Set cookie via map: ${cookie.name}=${cookie.value}, maxAge=${cookie.maxAge}, httpOnly=${cookie.httpOnly}"
    }

    def setCookieFull() {
        def cookie = cookieService.setCookie('full_cookie', 'full_value', 7200, null, null, null, false)
        render "Set full cookie: ${cookie.name}=${cookie.value}, maxAge=${cookie.maxAge}, httpOnly=${cookie.httpOnly}"
    }

    def setCookieDirectly() {
        Cookie c = new Cookie('direct_cookie', 'direct_value')
        c.maxAge = 1000
        c.path = '/'
        c.httpOnly = true
        cookieService.setCookie(c)
        render "Set direct cookie: ${c.name}=${c.value}"
    }

    def findCookieAction() {
        def cookie = cookieService.findCookie('demo_cookie')
        render "Found cookie: ${cookie?.name}=${cookie?.value}"
    }

    def setViaResponse() {
        def cookie = response.setCookie('response_cookie', 'response_value')
        render "Set via response: ${cookie.name}=${cookie.value}, maxAge=${cookie.maxAge}, path=${cookie.path}, httpOnly=${cookie.httpOnly}"
    }

    def getViaRequest() {
        def value = request.getCookie('demo_cookie')
        render "Got via request: demo_cookie=${value}"
    }
}
