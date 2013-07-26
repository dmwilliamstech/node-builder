package node.builder

import node.builder.bpm.ProcessEngineFactory
import org.activiti.engine.HistoryService
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.runtime.Execution



class MonitorGitJob {
    static triggers = {
      simple repeatInterval: (300 * 1000)l // execute job once in 5 minutes
    }

    def execute() {
        def repos
        try{
            repos = Repository.all
        }catch(e){
            log.error("Error retrieving repository data")
            return
        }

        repos.each{ Repository repo ->
            log.info("Running git monitor for repository ${repo.name}")
            RepositoryService repositoryService = ProcessEngineFactory.defaultProcessEngine().getRepositoryService();
            RuntimeService runtimeService = ProcessEngineFactory.defaultProcessEngine().getRuntimeService();

            // Deploy the process definition
            repositoryService.createDeployment()
                    .addClasspathResource("resources/monitor_git.bpmn20.xml")
                    .deploy();

            // Start a process instance
            def variables = new HashMap();
            def utilities = new Utilities();
            variables.put("remotePath", repo.remotePath)
            variables.put("localPath", repo.localPath)

            def processInstance = runtimeService.startProcessInstanceByKey("gitChangeMonitor", variables);

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
            //TODO record update in record
        }
    }
}
