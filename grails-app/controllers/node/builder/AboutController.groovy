package node.builder


/**
 * AboutController
 * A controller class handles incoming web requests and performs actions such as redirects, rendering views and so on.
 */
class AboutController {
    def grailsApplication

	static scaffold = false
	def index = {
        [
            dependencies: new XmlSlurper().parseText(this.class.classLoader.getResourceAsStream("dependency-report.xml").text),
            version: grailsApplication.metadata.'app.version',
            reference: this.class.classLoader.getResourceAsStream("reference.txt").text,
            name: grailsApplication.metadata.'app.name',
            dropFolder: [location: InputFileChangeListener.listener.directory.path]
        ]
    }
}
