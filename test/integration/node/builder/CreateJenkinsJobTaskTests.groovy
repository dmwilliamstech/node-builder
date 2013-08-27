package node.builder

import com.offbytwo.jenkins.JenkinsServer
import groovy.mock.interceptor.MockFor
import node.builder.bpm.CreateJenkinsJobTask
import org.activiti.engine.delegate.DelegateExecution
import org.junit.Test

class CreateJenkinsJobTaskTests {
    final shouldFail = new GroovyTestCase().&shouldFail

    def mockDelegateExecutionWithVariables(variables, variablecount, shouldSet){
        def delegateExecution = new MockFor(DelegateExecution.class)

        (0..variablecount).each {
            delegateExecution.demand.getVariable(){variable -> variables[variable]}
        }


        (0..shouldSet).each {
            delegateExecution.demand.setVariable(){name, value -> variables[name] = value}
        }

        return delegateExecution.proxyInstance()
    }

    void testSomething(){
        assert true
    }


    @Test
    void connectToJenkins(){
        def delegateExecution = mockDelegateExecutionWithVariables([jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99"], 4, 0)
        def createJenkinsJobTask = new CreateJenkinsJobTask()
        shouldFail(Exception){
            createJenkinsJobTask.execute(delegateExecution)
        }
    }


    @Test
    void createJobInJenkins(){
        def name = "test-job-" + UUID.randomUUID().toString()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:name, jenkinsJobXml:getJobXml()]
        def delegateExecution = mockDelegateExecutionWithVariables(variables , 5, 1)
        def createJenkinsJobTask = new CreateJenkinsJobTask()
        createJenkinsJobTask.execute(delegateExecution)
        assert variables.result.data.jenkinsJob.displayName == name
    }

    @Test
    void verifyMasterComputer(){
        JenkinsServer jenkins = new JenkinsServer(new URI("http://stackbox:9999/"),
                "admin", "foobar99")
        def computers = jenkins.computers
        assert computers.size() > 0
        assert computers.containsKey("master")
    }

    @Test
    void verifyMasterLabel(){
        JenkinsServer jenkins = new JenkinsServer(new URI("http://stackbox:9999/"),
                "admin", "foobar99")
        def label = jenkins.getLabel("master")
        assert label.nodes.size() == 1
        assert label.name == "master"
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
