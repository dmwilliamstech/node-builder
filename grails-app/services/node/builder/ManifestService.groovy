package node.builder

import node.builder.bpm.ProcessEngineFactory


/**
 * ManifestService
 * A service class encapsulates the core business logic of a Grails application
 */
class ManifestService {
    def groovyPagesTemplateEngine
    def instanceService

    static utilities = new Utilities()

    static transactional = true

    def deployToMasterAndProvision(manifestInstance, masterInstance){
        try{
            // Start a process instance
            def variables = new HashMap();
            def utilities = new Utilities();
            variables.put("manifest", utilities.serializeDomain(manifestInstance))
            variables.put("master", utilities.serializeDomain(masterInstance))
            return ProcessEngineFactory.runProcessWithVariables(ProcessEngineFactory.defaultProcessEngine("provision"), "provisionInstance", variables)
        }catch (e){
            e.printStackTrace()
            return [error: [message: e.getMessage()]]
        }

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

    def manifestForJson(manifest){
        def json = utilities.serializeDomain(manifest)
        json.manifest = manifest.manifest
        return json
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

    def deprovisionAndUndeployFromMaster(Manifest manifest, Master master, Deployment deployment) {
        try{
            // Start a process instance
            def variables = new HashMap();
            def utilities = new Utilities();

            variables.put("manifest", utilities.serializeDomain(manifest))
            variables.put("master", utilities.serializeDomain(master))
            variables.put("deployment", utilities.serializeDomain(deployment))

            def result = ProcessEngineFactory.runProcessWithVariables(ProcessEngineFactory.defaultProcessEngine("deprovision"), "deprovisionInstance", variables)


            manifest.removeFromDeployments(deployment)
            if(!result.error){
                deployment.instances.each{instance -> instance.delete()}
                deployment.delete()
            }

            return result
        }catch (e){
            e.printStackTrace()
            return [error: [message: e.getMessage()]]
        }
    }
}
