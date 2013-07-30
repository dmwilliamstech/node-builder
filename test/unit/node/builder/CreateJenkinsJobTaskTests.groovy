package node.builder

import groovy.mock.interceptor.MockFor
import node.builder.bpm.CreateJenkinsJobTask
import org.activiti.engine.delegate.DelegateExecution
import org.junit.Ignore
import org.junit.Test


class CreateJenkinsJobTaskTests {

    def mockDelegateExecutionWithVariables(variables, variablecount, shouldSet){
        def delegateExecution = new MockFor(DelegateExecution.class)

        (0..variablecount).each {
            delegateExecution.demand.getVariable(){variable -> variables[variable]}
        }

        if(shouldSet){
            (0..variablecount).each {
                delegateExecution.demand.setVariable(){name, value -> variables[name] = value}
            }
        }
        return delegateExecution.proxyInstance()
    }

    void testSomething(){
        assert true
    }

    @Ignore("Requires Jenkins")
    @Test
    void connectToJenkins(){
        def delegateExecution = mockDelegateExecutionWithVariables([jenkinsUrl: "http://localhost:9090/", jenkinsUser:"admin", jenkinsPassword:"admin"], 4, false)
        def createJenkinsJobTask = new CreateJenkinsJobTask()
        createJenkinsJobTask.execute(delegateExecution)
    }

    @Ignore("Requires Jenkins")
    @Test
    void createJobInJenkins(){
        def delegateExecution = mockDelegateExecutionWithVariables([jenkinsUrl: "http://localhost:9090/", jenkinsUser:"admin", jenkinsPassword:"admin", jenkinsJobName:"test-job-" + UUID.randomUUID().toString(), jenkinsJobXml:getJobXml()], 5, false)
        def createJenkinsJobTask = new CreateJenkinsJobTask()
        createJenkinsJobTask.execute(delegateExecution)
    }

    def getJobXml(){
        return """\
<?xml version='1.0' encoding='UTF-8'?>
<project>
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>true</canRoam>
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
    }
}
