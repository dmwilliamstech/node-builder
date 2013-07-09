package node.builder

import grails.converters.JSON
import org.activiti.engine.HistoryService
import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.runtime.Execution
import org.codehaus.groovy.grails.io.support.GrailsResourceUtils

/**
 * ManifestService
 * A service class encapsulates the core business logic of a Grails application
 */
class ManifestService {
    def groovyPagesTemplateEngine
    static ProcessEngine pe


    static transactional = true

    def processEngine(){
        if(pe == null)
            pe= ProcessEngineConfiguration
                    .createStandaloneInMemProcessEngineConfiguration()
                    .buildProcessEngine();
        return pe
    }


    def deployToMasterAndProvision(manifestInstance, masterInstance){
        try{


            // Get Activiti services
            RepositoryService repositoryService = processEngine().getRepositoryService();
            RuntimeService runtimeService = processEngine().getRuntimeService();

            // Deploy the process definition
            repositoryService.createDeployment()
                    .addClasspathResource("resources/provision_instance.bpmn20.xml")
                    .deploy();

            // Start a process instance
            def variables = new HashMap();
            def utilities = new Utilities();
            variables.put("manifest", utilities.serializeDomain(manifestInstance))
            variables.put("master", utilities.serializeDomain(masterInstance))
            def processInstance = runtimeService.startProcessInstanceByKey("provisionInstance", variables);

            // verify that the process is actually finished

            def result = runtimeService.getVariable(processInstance.getId(), "error") ?: runtimeService.getVariable(processInstance.getId(), "result")

            Execution execution = runtimeService.createExecutionQuery()
                    .processInstanceId(processInstance.getId())
                    .activityId("receiveTask")
                    .singleResult();
            runtimeService.signal(execution.getId());

            HistoryService historyService = processEngine().getHistoryService();
            HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();

            return result
        }catch (e){
            return [error: [message: e.getMessage()]]
        }

    }

    def deployTo(manifestInstance, masterInstance) {

        def scpFileCopier = new SCPFileCopier()
        def key = new File(masterInstance.privateKey.replaceAll("\\~",System.getenv()["HOME"]))
        for(instance in manifestInstance.manifest.instances){
            def node = new File(instance.name.toString().replaceAll(/\s/, '-') + '.pp')
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

            def instanceName = instance.name.replaceAll(/\s/, '-')
            if(Instance.findByName(instanceName)){
                return [error: [message: "Instance ${instanceName} already exists please update to continue"]]
            }
            def instanceData =  OpenStackConnection.connection.launch(instance.flavorId, manifest.manifest.imageId, instanceName)
            def server = instanceData.server
            if(server != null){
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
            }else{
                return instanceData
            }
        }
        return manifest
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
