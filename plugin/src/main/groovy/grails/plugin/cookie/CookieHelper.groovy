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

import grails.core.GrailsApplication
import grails.util.Holders
import grails.web.api.ServletAttributes
import grails.web.api.WebAttributes
import groovy.util.logging.Slf4j
import org.grails.web.servlet.mvc.GrailsWebRequest

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

@Slf4j
class CookieHelper implements ServletAttributes, WebAttributes {

    /** 30 days in seconds */
    static final int DEFAULT_COOKIE_AGE = 30 * 24 * 60 * 60
    static final boolean COOKIE_DEFAULT_HTTP_ONLY = true

    @Override
    GrailsApplication getGrailsApplication() {
        GrailsApplication fromRequest = GrailsWebRequest.lookup()?.applicationContext?.getBean(GrailsApplication)
        fromRequest ?: Holders.grailsApplication
    }

    Cookie createCookie(String name, String value, Integer maxAge, String path, String domain, Boolean secure, Boolean httpOnly) {
        Cookie cookie = new Cookie(name, value)
        cookie.path = getDefaultCookiePath(path)
        cookie.maxAge = getDefaultCookieAge(maxAge)
        if (domain) {
            cookie.domain = domain
        }
        cookie.secure = getDefaultCookieSecure(secure)
        cookie.httpOnly = getDefaultCookieHttpOnly(httpOnly)
        cookie
    }

    /**
     * Default expiration age for cookie in seconds. {@code Max-Age} attribute.
     * If {@code -1} the cookie will not be stored and is removed after browser close.
     * If null or unset, will use 30 days (2592000 seconds).
     * Cannot be 0 since that means delete the cookie.
     */
    int getDefaultCookieAge(Integer maxAge) {
        if (maxAge != null) return maxAge
        Integer configValue = grailsApplication?.config?.getProperty('grails.plugins.cookie.cookieage.default', Integer)
        configValue != null ? configValue : DEFAULT_COOKIE_AGE
    }

    /**
     * Default path for cookie selection strategy.
     * {@code 'context'} - web app context path (default)
     * {@code 'root'} - root of server, i.e. '/'
     * {@code 'current'} - current directory, i.e. controller name
     */
    String getDefaultCookiePath(String path) {
        if (path) return path
        String strategy = grailsApplication?.config?.getProperty('grails.plugins.cookie.path.defaultStrategy')
        if (strategy == 'root') return '/'
        if (strategy == 'current') return null
        request?.contextPath
    }

    /** If default secure is null or unset, mirrors {@code request.secure} */
    boolean getDefaultCookieSecure(Boolean secure) {
        if (secure != null) return secure
        String configValue = grailsApplication?.config?.getProperty('grails.plugins.cookie.secure.default')
        configValue != null ? configValue.toBoolean() : request.secure
    }

    /** Default HTTP Only flag — denies access from JavaScript's {@code document.cookie}. Defaults to {@code true}. */
    boolean getDefaultCookieHttpOnly(Boolean httpOnly) {
        if (httpOnly != null) return httpOnly
        String configValue = grailsApplication?.config?.getProperty('grails.plugins.cookie.httpOnly.default')
        configValue != null ? configValue.toBoolean() : COOKIE_DEFAULT_HTTP_ONLY
    }

    void writeCookieToResponse(HttpServletResponse response, Cookie cookie) {
        response.addCookie cookie
        log.trace "cookie set: ${cookie.name}, Max-Age: ${cookie.maxAge}, Path: ${cookie.path}, Domain: ${cookie.domain}, HttpOnly: ${cookie.httpOnly}, Secure: ${cookie.secure}"
    }
}
