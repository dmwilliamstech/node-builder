package node.builder

import grails.converters.JSON
import node.builder.bpm.ProcessEngineFactory
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

    static utilities = new Utilities()

    static transactional = true

    def deployToMasterAndProvision(manifestInstance, masterInstance){
        try{


            // Get Activiti services
            RepositoryService repositoryService = ProcessEngineFactory.defaultProcessEngine().getRepositoryService();
            RuntimeService runtimeService = ProcessEngineFactory.defaultProcessEngine().getRuntimeService();

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

            HistoryService historyService = ProcessEngineFactory.defaultProcessEngine().getHistoryService();
            HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();

            return result
        }catch (e){
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


            // Get Activiti services
            RepositoryService repositoryService = ProcessEngineFactory.defaultProcessEngine().getRepositoryService();
            RuntimeService runtimeService = ProcessEngineFactory.defaultProcessEngine().getRuntimeService();

            // Deploy the process definition
            repositoryService.createDeployment()
                    .addClasspathResource("resources/deprovision_instance.bpmn20.xml")
                    .deploy();

            // Start a process instance
            def variables = new HashMap();
            def utilities = new Utilities();
            variables.put("manifest", utilities.serializeDomain(manifest))

            variables.put("master", utilities.serializeDomain(master))

            variables.put("deployment", utilities.serializeDomain(deployment))
            def processInstance = runtimeService.startProcessInstanceByKey("deprovisionInstance", variables);

            // verify that the process is actually finished

            def result = runtimeService.getVariable(processInstance.getId(), "error") ?: runtimeService.getVariable(processInstance.getId(), "result")

            Execution execution = runtimeService.createExecutionQuery()
                    .processInstanceId(processInstance.getId())
                    .activityId("receiveTask")
                    .singleResult();
            runtimeService.signal(execution.getId());

            HistoryService historyService = ProcessEngineFactory.defaultProcessEngine().getHistoryService();
            HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
            manifest.removeFromDeployments(deployment)
            deployment.delete()
            return result
        }catch (e){
            return [error: [message: e.getMessage()]]
        }
    }
}
