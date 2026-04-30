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
        [name: 'Christian Oestreich', email: 'acetrike@gmail.com'],
        [name: 'Dale Wiggins', email: 'dale@dalew.com'],
        [name: 'Sergey Ponomarev', email: 'stokito@gmail.com'],
        [name: 'Søren Berg Glasius', email: 'soeren@glasius.dk'],
    ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/gpc/grails-cookie/issues']
    def scm = [url: 'https://github.com/gpc/grails-cookie']
}
