package node.builder

import org.codehaus.groovy.grails.io.support.GrailsResourceUtils

/**
 * ManifestService
 * A service class encapsulates the core business logic of a Grails application
 */
class ManifestService {
    def groovyPagesTemplateEngine

    static transactional = false

    def deployTo(manifestInstance, masterInstance) {

        def scpFileCopier = new SCPFileCopier()
        def key = new File(masterInstance.privateKey.replaceAll("\\~",System.getenv()["HOME"]))

        def node = new File(manifestInstance.manifest.instanceName.toString() + '.pp')
        node.write(processTemplate(manifestInstance, "${GrailsResourceUtils.VIEWS_DIR_PATH}/templates/node.pp.gsp"))
        scpFileCopier.copyTo(node, masterInstance.hostname, new File(masterInstance.remotePath), masterInstance.username, key)
    }


    def processTemplate(manifestInstance, template){
        def output = new StringWriter()
        def templateText = new File(template).text
        groovyPagesTemplateEngine.createTemplate(templateText, 'node.pp').make([manifest: manifestInstance.manifest, items: ['Grails','Groovy']]).writeTo(output)
        return output.getBuffer().toString()
    }
}
