package node.builder

import node.builder.bpm.ProcessEngineFactory
import node.builder.bpm.ProcessResult


import java.util.concurrent.Callable
import java.util.concurrent.Executors



class MonitorGitJob {
    static triggers = {
      simple repeatInterval: (30 * 1000)l // execute job once in 5 minutes
    }

    static def pool
    static Map futures = [:]

    def MonitorGitJob(){
        if(pool == null)
            pool = Executors.newCachedThreadPool()
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
            if(futures.get(repo.name) == null || futures.get(repo.name).isDone()){
            futures.put(repo.name, pool.submit(new Callable<ProcessResult>() {
                public ProcessResult call() {
                    log.info("Running git monitor for repository ${repo.name}")
                    def processEngine = ProcessEngineFactory.defaultProcessEngine(repo.name)

                    def variables = new HashMap();
                    variables.put("remotePath", repo.remotePath)
                    variables.put("localPath", repo.localPath)

                    def result = ProcessEngineFactory.runProcessWithVariables(processEngine, repo.workflowKey, variables)

                    log.info("Finished monitoring for repository, ${(result.data.repositoryDidChange? "change":"no change")} detected")
                }}))
            }
        }
    }
}
