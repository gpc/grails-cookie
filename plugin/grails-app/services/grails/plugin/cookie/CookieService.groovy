/*
 * Copyright 2012 the original author or authors
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

import grails.web.api.ServletAttributes

import jakarta.servlet.http.Cookie

class CookieService implements ServletAttributes {

    /**
     * Gets the value of the named cookie.
     * @param name Case-sensitive cookie name
     * @return Returns cookie value or null if cookie does not exist
     */
    String getCookie(String name) {
        request.getCookie name
    }

    /**
     * Gets the named cookie
     * @param name Case-sensitive cookie name
     * @return null if cookie not found
     */
    Cookie findCookie(String name) {
        request.findCookie name
    }

    /**
     * Sets the cookie with name to value, with age in seconds
     * @param name Cookie name. Can't be blank or null and is case-sensitive
     * @param value Cookie value.
     * @param maxAge Age to store cookie in seconds; if negative, means the cookie is not stored; if zero, deletes the cookie.
     * @param path A path to which the client should return the cookie.
     * @param domain Domain name by RFC 6265.
     * @param secure Indicates to the browser whether the cookie should only be sent using a secure protocol.
     * @param httpOnly Denies accessing to JavaScript's {@code document.cookie}.
     */
    Cookie setCookie(String name, String value, Integer maxAge = null, String path = null, String domain = null, Boolean secure = null, Boolean httpOnly = null) {
        response.setCookie name, value, maxAge, path, domain, secure, httpOnly
    }

    /**
     * Sets the cookie with name to value, with age in seconds
     * @param args Named params e.g. {@code [name: 'cookie_name', value: 'some_val', secure: true]}
     */
    Cookie setCookie(Map args) {
        assert args
        response.setCookie args.name, args.value, args.maxAge, args.path, args.domain, args.secure?.toString()?.toBoolean(), args.httpOnly?.toString()?.toBoolean()
    }

    /** Sets the cookie. Note: it doesn't set defaults */
    Cookie setCookie(Cookie cookie) {
        response.setCookie cookie
    }

    /** Deletes the named cookie */
    Cookie deleteCookie(String name, String path = null, String domain = null) {
        assert name
        response.deleteCookie name, path, domain
    }

    /** Deletes the named cookie */
    Cookie deleteCookie(Cookie cookie) {
        assert cookie
        response.deleteCookie cookie
    }
}
