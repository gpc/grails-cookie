/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.cookie

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Slf4j
@CompileStatic
class RequestResponseCookieExtension {

    static final int COOKIE_AGE_TO_DELETE = 0

    private static final CookieHelper helper = new CookieHelper()

    static Cookie findCookie(HttpServletRequest request, String name) {
        assert name
        request.getCookies()?.find { it != null && it.name == name }
    }

    static String getCookie(HttpServletRequest request, String name) {
        assert name
        String cookieValue = findCookie(request, name)?.value
        log.info(cookieValue ? "Found cookie \"${name}\", value = \"${cookieValue}\"" : "No cookie found with name: \"${name}\"")
        cookieValue
    }

    /**
     * Sets the cookie with name to value, with age in seconds
     * @param name Cookie name. Can't be blank or null and is case-sensitive
     * @param value Cookie value.
     * @param maxAge Age to store cookie in seconds; if negative, means the cookie is not stored; if zero, deletes the cookie.
     * @param path A path to which the client should return the cookie. See RFC 6265.
     * @param domain Domain name by RFC 6265.
     * @param secure Indicates to the browser whether the cookie should only be sent using a secure protocol.
     * @param httpOnly Denies accessing to JavaScript's {@code document.cookie}, mitigating XSS attacks.
     */
    static Cookie setCookie(HttpServletResponse response, String name, String value, Integer maxAge = null, String path = null, String domain = null, Boolean secure = null, Boolean httpOnly = null) {
        setCookie response, helper.createCookie(name, value, maxAge, path, domain, secure, httpOnly)
    }

    /**
     * Sets the cookie with name to value, with age in seconds
     * @param args Named params e.g. {@code [name: 'cookie_name', value: 'some_val', secure: true]}
     */
    @SuppressWarnings('GroovyUnusedDeclaration')
    static Cookie setCookie(HttpServletResponse response, Map args) {
        assert args
        Boolean secure = args.secure?.toString()?.toBoolean()
        Boolean httpOnly = args.httpOnly?.toString()?.toBoolean()
        setCookie response, helper.createCookie(args.name as String, args.value as String, args.maxAge as Integer, args.path as String, args.domain as String, secure != null ? secure : false, httpOnly != null ? httpOnly : true)
    }

    /** Sets the cookie. Note: it doesn't set defaults */
    static Cookie setCookie(HttpServletResponse response, Cookie cookie) {
        assert cookie
        log.info 'Setting cookie'
        helper.writeCookieToResponse response, cookie
        cookie
    }

    /** Deletes the named cookie */
    static Cookie deleteCookie(HttpServletResponse response, String name, String path = null, String domain = null) {
        assert name
        log.info 'Removing cookie'
        Cookie cookie = helper.createCookie(name, null, COOKIE_AGE_TO_DELETE, path, domain, null, null)
        helper.writeCookieToResponse response, cookie
        cookie
    }

    /** Deletes the named cookie */
    static Cookie deleteCookie(HttpServletResponse response, Cookie cookie) {
        assert cookie
        deleteCookie response, cookie.name, cookie.path, cookie.domain
    }
}
