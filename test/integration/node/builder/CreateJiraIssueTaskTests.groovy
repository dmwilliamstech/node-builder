package node.builder

import groovy.mock.interceptor.MockFor
import node.builder.bpm.CreateJiraIssueTask
import org.activiti.engine.delegate.DelegateExecution
import org.junit.Before


class CreateJiraIssueTaskTests {
    final shouldFail = new GroovyTestCase().&shouldFail

    @Before
    void setup(){

    }

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

    void testCreateIssue(){
        def variables = [jiraUrl: "http://stackbox:8181/",
                jiraUser:"test",
                jiraPassword:"test99",
                jiraProject: "TST",
                jiraIssueType: 1l,
                jiraIssueSummary: "It's all broken!",
                jiraIssueDescription: "You really stuffed it up, didn't you?"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, variables.size(), 1)
        def createJiraIssueTask = new CreateJiraIssueTask()

        createJiraIssueTask.execute(delegateExecution)

        assert variables.result.data.jiraIssueKey.matches(/TST-\d+/)
    }
}
