package node.builder.bpm

import node.builder.Utilities
import org.activiti.engine.HistoryService
import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.ProcessEngines
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.repository.Deployment
import org.activiti.engine.runtime.Execution


class ProcessEngineFactory {
    static private final $lock = new Object[0]

    private ProcessEngineFactory(){
    }

    public static ProcessEngine defaultProcessEngine(String name){
        synchronized ($lock){
            def processEngine = ProcessEngines.getProcessEngine(name)
            if(processEngine  == null){
                processEngine = ProcessEngineConfiguration
                        .createStandaloneProcessEngineConfiguration()
                        .setJdbcUrl("jdbc:h2:activiti;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE")
                        .setDatabaseSchemaUpdate("true")
                        .setProcessEngineName(name)
                        .buildProcessEngine();
                ProcessEngines.registerProcessEngine(processEngine)
            }
            return processEngine
        }
    }

    public static Deployment deployProcessDefinitionFromUrlWithProcessEngine(String url, String engineName, String deploymentName = null){
        ProcessEngine processEngine = ProcessEngineFactory.defaultProcessEngine(engineName)
        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(url)
                .name(deploymentName)
                .deploy();
        assert deployment != null

        return deployment
    }

    public static def runProcessWithVariables(ProcessEngine processEngine, String processKey, Map variables){
        RuntimeService runtimeService = processEngine.getRuntimeService();
        def processInstance
        synchronized($lock) {
            // Start a process instance
            processInstance = runtimeService.startProcessInstanceByKey(processKey, variables);
        }
        // verify that the process is actually finished

        def result = runtimeService.getVariable(processInstance.getId(), "error") ?: runtimeService.getVariable(processInstance.getId(), "result")

        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getId())
                .activityId("receiveTask")
                .singleResult();
        runtimeService.signal(execution.getId());

        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance =
            historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();

        return result
    }
}