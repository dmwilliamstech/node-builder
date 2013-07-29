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
            def processEngine = ProcessEngineFactory.defaultProcessEngine(repo.name)
            def variables = new HashMap();
            variables.put("remotePath", repo.remotePath)
            variables.put("localPath", repo.localPath)
            def result = ProcessEngineFactory.runProcessWithVariables(processEngine, "gitChangeMonitor", variables)
            //TODO record update in record
        }
    }
}
