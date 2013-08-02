package node.builder

import grails.converters.XML

/**
 * DependencyReportController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class DependencyReportController {
    static scaffold = false

    def index() {
        def text = this.class.classLoader.getResourceAsStream("dependency-report.xml").text
        render(text:text,contentType:"text/xml",encoding:"UTF-8")
    }
}
