/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package node.builder

import node.builder.bpm.ProcessEngineFactory
import node.builder.bpm.ProcessResult


import java.util.concurrent.Callable
import java.util.concurrent.Executors



class MonitorGitJob {
    static triggers = {
      simple repeatInterval: (30l * 1000l) // execute job once in 5 minutes
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
            repos = Project.findAllByProjectType(ProjectType.findByName("GIT Repository"))
        }catch(e){
            log.error("Error retrieving repository data")
            return
        }

        repos.each{ Project repo ->
            if((repo.active) && (futures.get(repo.name) == null || futures.get(repo.name).isDone())){
            futures.remove(repo.name)
            futures.put(repo.name, pool.submit(new Callable<ProcessResult>() {
                public ProcessResult call() {
                    try{
                        log.info("Running git monitor for repository ${repo.name}")
                        def processEngine = ProcessEngineFactory.defaultProcessEngine(repo.name)

                        def variables = new HashMap();
                        variables.put("projectName", repo.name)
                        variables.put("remotePath", repo.location)
                        variables.put("localPath", "${Config.getGlobalConfig().get("workspace.path")}/${repo.name.replaceAll("\\W", "").toLowerCase()}")

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

                        def businessKey = "${repo.name}-${java.util.UUID.randomUUID()}"
                        def result = ProcessEngineFactory.runProcessWithBusinessKeyAndVariables(processEngine, repo.processDefinitionKey, businessKey, variables)

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
    <scm class="hudson.plugins.git.GitSCM" plugin="git@1.4.0">
        <configVersion>2</configVersion>
        <userRemoteConfigs>
            <hudson.plugins.git.UserRemoteConfig>
                <name></name>
                <refspec></refspec>
                <url>https://github.com/kellyp/testy.git</url>
            </hudson.plugins.git.UserRemoteConfig>
        </userRemoteConfigs>
        <branches>
            <hudson.plugins.git.BranchSpec>
                <name>**</name>
            </hudson.plugins.git.BranchSpec>
        </branches>
        <disableSubmodules>false</disableSubmodules>
        <recursiveSubmodules>false</recursiveSubmodules>
        <doGenerateSubmoduleConfigurations>false</doGenerateSubmoduleConfigurations>
        <authorOrCommitter>false</authorOrCommitter>
        <clean>false</clean>
        <wipeOutWorkspace>false</wipeOutWorkspace>
        <pruneBranches>false</pruneBranches>
        <remotePoll>false</remotePoll>
        <ignoreNotifyCommit>false</ignoreNotifyCommit>
        <useShallowClone>false</useShallowClone>
        <buildChooser class="hudson.plugins.git.util.DefaultBuildChooser"/>
        <gitTool>Default</gitTool>
        <submoduleCfg class="list"/>
        <relativeTargetDir></relativeTargetDir>
        <reference></reference>
        <excludedRegions></excludedRegions>
        <excludedUsers></excludedUsers>
        <gitConfigName></gitConfigName>
        <gitConfigEmail></gitConfigEmail>
        <skipTag>false</skipTag>
        <includedRegions></includedRegions>
        <scmName></scmName>
    </scm>
    <canRoam>true</canRoam>
    <disabled>false</disabled>
    <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
    <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
    <triggers/>
    <concurrentBuild>false</concurrentBuild>
    <builders>
        <hudson.tasks.Shell>
            <command>./test</command>
        </hudson.tasks.Shell>
    </builders>
    <publishers/>
    <buildWrappers/>
</project>
"""
    }
}
