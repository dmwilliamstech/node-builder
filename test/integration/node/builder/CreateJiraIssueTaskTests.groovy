package node.builder

import groovy.mock.interceptor.MockFor
import node.builder.bpm.CreateJiraIssueTask
import org.activiti.engine.delegate.DelegateExecution
import org.junit.Before
import org.junit.Ignore


class CreateJiraIssueTaskTests extends BPMNTaskTestBase{
    final shouldFail = new GroovyTestCase().&shouldFail

    @Before
    void setup(){

    }

    @Ignore
    void testCreateIssue(){
        def variables = [jiraUrl: "http://stackbox:8181/",
                jiraUser:"admin",
                jiraPassword:"foobar99",
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
