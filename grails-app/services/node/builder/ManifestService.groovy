package node.builder

import grails.converters.JSON
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
        return output.getBuffer().toString().replaceAll("\\-\\>\\s+\\}", "\n}")
    }

    def provision(Manifest manifest) {
        def instanceData =  OpenStackConnection.connection.launch("1", manifest.manifest.imageId, manifest.manifest.instanceName)
        def server = instanceData.server
        def instance = new Instance(
                name: server.name,
                status: server.status,
                hostId: server.hostId,
                privateIP: (server.addresses?.private?.getAt(0)?.addr ?: "not set"),
                keyName: server.key_name.toString(),
                flavorId: server.flavor.id,
                instanceId: server.id,
                userId: server.user_id,
                tenantId: server.tenant_id,
                progress: server.progress,
                configDrive: server.config_drive ?: "not set",
                metadata: (server.metadata as JSON).toString(),
                image: Image.findByImageId(server.image.id)
        )
        instance.save(failOnError: true)
        manifest.addToInstances(instance)
    }
}
