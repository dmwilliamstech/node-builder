package node.builder.bpm

import com.atlassian.jira.rest.client.api.IssueRestClient
import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.BasicIssue
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import com.atlassian.util.concurrent.Function
import com.atlassian.util.concurrent.Promise
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate

class CreateJiraIssueTask extends JenkinsJobTask implements JavaDelegate{
    void execute(DelegateExecution delegateExecution) throws Exception {
        log.info "Starting Jira issue creation"
        ProcessResult result = delegateExecution.getVariable("result")?: new ProcessResult()
        def jiraUrl = delegateExecution.getVariable("jiraUrl")
        def jiraServerUri = URI.create(jiraUrl)
        def jiraUser = delegateExecution.getVariable("jiraUser")
        def jiraPassword = delegateExecution.getVariable("jiraPassword")

        final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory()
        final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, jiraUser, jiraPassword)

        try {
            final IssueRestClient issueClient = restClient.getIssueClient();

            log.info("Sending issue creation request");

            def issueSummary =  delegateExecution.getVariable("jiraIssueSummary")
            def issueProject =  delegateExecution.getVariable("jiraProject")
            def issueType =  delegateExecution.getVariable("jiraIssueType")
            def issueDescription =  delegateExecution.getVariable("jiraIssueDescription")

            final IssueInput newIssue = new IssueInputBuilder(issueProject, issueType, issueSummary)
                    .setDescription(issueDescription)
                    .build()
            log.info("Creating issue ${issueSummary} ")
            Promise<BasicIssue> promise = issueClient.createIssue(newIssue)

            log.info("Verifying issue was created")
            def issue = promise.claim()
            result.data.jiraIssueKey = issue.key
            result.data.jiraIssueUrl = "${jiraUrl}/browse/${issue.key}"

            log.info("Created issue ${issue.key}")
        } finally {
            restClient.close();
        }

        delegateExecution.setVariable("result", result)

        log.info "Jira issue creation completed"
    }
}
