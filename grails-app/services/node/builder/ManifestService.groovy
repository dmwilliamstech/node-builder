package node.builder

import grails.converters.JSON
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils

/**
 * ManifestService
 * A service class encapsulates the core business logic of a Grails application
 */
class ManifestService {
    def groovyPagesTemplateEngine

    static transactional = true

    def deployTo(manifestInstance, masterInstance) {

        def scpFileCopier = new SCPFileCopier()
        def key = new File(masterInstance.privateKey.replaceAll("\\~",System.getenv()["HOME"]))
        for(instance in manifestInstance.manifest.instances){
            def node = new File(instance.name.toString() + '.pp')
            node.write(processTemplate(instance, "${GrailsResourceUtils.VIEWS_DIR_PATH}/templates/node.pp.gsp"))
            scpFileCopier.copyTo(node, masterInstance.hostname, new File(masterInstance.remotePath), masterInstance.username, key)
        }
    }


    def processTemplate(instance, template){
        def output = new StringWriter()
        def templateText = new File(template).text
        groovyPagesTemplateEngine.createTemplate(templateText, 'node.pp').make([manifest: instance, service: this]).writeTo(output)
        return output.getBuffer().toString().replaceAll("\\-\\>\\s+\\}", "\n}")
    }

    def processConfigurationValue(value){
        if(value.class == java.lang.String){
            return "\"${value}\""
        }else{
            String string = "[ "
            for(config in value){
                string += "\"" + config + "\", "
            }
            string = string.replaceAll(/\,\s$/, "")
            string += " ]"
            return string
        }
    }

    def provision(Manifest manifest) {
        for(instance in manifest.manifest.instances){
            def instanceData =  OpenStackConnection.connection.launch(instance.flavorId, manifest.manifest.imageId, instance.name)
            def server = instanceData.server
            def instanceDomain = new Instance(
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
            instanceDomain.save(failOnError: true)
            manifest.addToInstances(instanceDomain)
        }
    }

    def generateGraph(id) {
        def manifest = Manifest.get(id)
        def graph = [name: manifest.name, children: []]
        for(instance in manifest.manifest.instances){
            def instanceGraph = [name: instance.name]
            instanceGraph.children = (generateNodesGraph(instance))
            instanceGraph.children += (generateAppGraph(instance))
            instanceGraph.children += [[name: Flavor.get(instance.flavorId).name, size: 1]]
            graph.children.add(instanceGraph)
        }
        return graph
    }

    def generateConfigGraph(object){
        def configs = []
        for(configValue in object.configurations){
            def config = [name: configValue.name, children: []]
            if(configValue.value.json.class == String){
                config.children.add([name: configValue.value.json, size: 1])
            }else{
                for(value in configValue.value.json){
                    config.children.add([name: ((String)value), size: 1])
                }

            }
            configs.add(config)
        }
        return configs
    }

    def generateAppGraph(instance){
        def apps = []
        for(appId in instance.applications){
            def app = Application.get(appId.key)

            apps.add([name: app.name, children: generateConfigGraph(app)])

        }
        return apps
    }

    def generateNodesGraph(instance){
        def nodes = []
        for(nodeId in instance.nodes){
            def node = Node.get(nodeId.key)
            nodes.add([name: node.name, children: generateConfigGraph(node)])
        }
        return nodes
    }
}
