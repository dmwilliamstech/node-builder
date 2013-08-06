package node.builder

import com.offbytwo.jenkins.model.BuildResult
import groovy.mock.interceptor.MockFor
import node.builder.bpm.RunJenkinsJobTask
import org.activiti.engine.delegate.DelegateExecution
import org.junit.Test

class RunJenkinsJobTaskTests {

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

    def testSomething(){
        assert true
    }


    @Test
    void runTestJob(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"test"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 4, 1)
        jenkinsTask.execute(delegateExecution)
        assert variables.result.data.jenkinsBuild.result == BuildResult.SUCCESS
    }


    @Test
    void runFailJob(){
        def jenkinsTask = new RunJenkinsJobTask()
        def variables = [jenkinsUrl: "http://stackbox:9999/", jenkinsUser:"admin", jenkinsPassword:"foobar99", jenkinsJobName:"fail"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 4, 1)
        jenkinsTask.execute(delegateExecution)
        assert variables.result.data.jenkinsBuild.result == BuildResult.FAILURE
    }
}
