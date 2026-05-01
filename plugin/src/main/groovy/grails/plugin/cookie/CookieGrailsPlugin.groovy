package grails.plugin.cookie

import grails.plugins.Plugin

class CookieGrailsPlugin extends Plugin {

    def grailsVersion = '7.0.0 > *'
    def author = 'Christian Oestreich'
    def authorEmail = 'acetrike@gmail.com'
    def title = 'Cookie Plugin'
    def description = 'Makes dealing with cookies easy. Provides an injectable service and expands request/response with methods to easily get, set, and delete cookies with one line. RFC 6265 compliant.'
    def documentation = 'https://github.com/gpc/grails-cookie'
    def license = 'APACHE'
    def developers = [
        [name: 'Grails Plugin Collective', url: 'https://github.com/gpc/grails-cookie/graphs/contributors'],
    ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/gpc/grails-cookie/issues']
    def scm = [url: 'https://github.com/gpc/grails-cookie']
}
