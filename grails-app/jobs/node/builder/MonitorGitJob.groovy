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
            futures.remove(repo.name)
            futures.put(repo.name, pool.submit(new Callable<ProcessResult>() {
                public ProcessResult call() {
                    try{
                        log.foo("Running git monitor for repository ${repo.name}\n")
                        def processEngine = ProcessEngineFactory.defaultProcessEngine(repo.name)

                        def variables = new HashMap();
                        variables.put("projectName", repo.name)
                        variables.put("remotePath", repo.remotePath)
                        variables.put("localPath", repo.localPath)

                        variables.put("jenkinsUrl", "http://stackbox:9999/")
                        variables.put("jenkinsUser", "admin")
                        variables.put("jenkinsPassword", "foobar99")

                        //TODO move jobXml to input
                        variables.put("jenkinsJobXml", getJobXml())

                        variables.put("jiraUrl", "http://stackbox:8181/")
                        variables.put("jiraUser", "test")
                        variables.put("jiraPassword", "test99")
                        variables.put("jiraProject", "TST")
                        variables.put("jiraIssueType", 1l)

                        def result = ProcessEngineFactory.runProcessWithVariables(processEngine, repo.workflowKey, variables)

                        log.info("Finished monitoring for repository, ${(result.data.repositoryDidChange? "change":"no change")} detected")
                    }catch(e){
                        log.error("Error in git monitor thread ${e}")
                        e.printStackTrace()
                    }
                }}))
            }
        }
    }

    def getJobXml(){
          return """\
<?xml version='1.0' encoding='UTF-8'?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <jenkins.plugins.hipchat.HipChatNotifier_-HipChatJobProperty plugin="hipchat@0.1.4">
      <room></room>
      <startNotification>false</startNotification>
    </jenkins.plugins.hipchat.HipChatNotifier_-HipChatJobProperty>
  </properties>
  <scm class="hudson.scm.NullSCM"/>
  <assignedNode>expando</assignedNode>
  <canRoam>false</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders/>
  <publishers/>
  <buildWrappers/>
</project>
"""

//        return """\
//<?xml version='1.0' encoding='UTF-8'?>
//<project>
//    <actions/>
//    <description></description>
//    <keepDependencies>false</keepDependencies>
//    <properties>
//        <jenkins.plugins.hipchat.HipChatNotifier_-HipChatJobProperty plugin="hipchat@0.1.4">
//            <room></room>
//            <startNotification>false</startNotification>
//        </jenkins.plugins.hipchat.HipChatNotifier_-HipChatJobProperty>
//    </properties>
//
//    <scm class="hudson.plugins.git.GitSCM" plugin="git@1.4.0">
//        <configVersion>2</configVersion>
//        <userRemoteConfigs>
//            <hudson.plugins.git.UserRemoteConfig>
//                <name></name>
//                <refspec></refspec>
//                <url>https://github.com/kellyp/testy.git</url>
//            </hudson.plugins.git.UserRemoteConfig>
//        </userRemoteConfigs>
//        <branches>
//            <hudson.plugins.git.BranchSpec>
//                <name>**</name>
//            </hudson.plugins.git.BranchSpec>
//        </branches>
//        <disableSubmodules>false</disableSubmodules>
//        <recursiveSubmodules>false</recursiveSubmodules>
//        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
//        <authorOrCommitter>false</authorOrCommitter>
//        <clean>false</clean>
//        <wipeOutWorkspace>false</wipeOutWorkspace>
//        <pruneBranches>false</pruneBranches>
//        <remotePoll>false</remotePoll>
//        <ignoreNotifyCommit>false</ignoreNotifyCommit>
//        <useShallowClone>false</useShallowClone>
//        <buildChooser class="hudson.plugins.git.util.DefaultBuildChooser"/>
//        <gitTool>Default</gitTool>
//        <submoduleCfg class="list"/>
//        <relativeTargetDir></relativeTargetDir>
//        <reference></reference>
//        <excludedRegions></excludedRegions>
//        <excludedUsers></excludedUsers>
//        <gitConfigName></gitConfigName>
//        <gitConfigEmail></gitConfigEmail>
//        <skipTag>false</skipTag>
//        <includedRegions></includedRegions>
//        <scmName></scmName>
//    </scm>
//    <canRoam>true</canRoam>
//    <disabled>false</disabled>
//    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
//    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
//    <triggers/>
//    <concurrentBuild>false</concurrentBuild>
//    <builders>
//        <hudson.tasks.Shell>
//            <command>./test</command>
//        </hudson.tasks.Shell>
//    </builders>
//    <publishers/>
//    <buildWrappers/>
//</project>
//"""
    }
}
