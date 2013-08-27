package node.builder

import node.builder.bpm.WriteSmtpMessagesTask
import org.junit.Test

class WriteSmtpMessagesTaskTests extends BPMNTaskTestBase{

    /**
     def host = delegateExecution.getVariable("emailSmtpHost")
     def port = delegateExecution.getVariable("emailSmtpPort")
     def username = delegateExecution.getVariable("emailUsername")
     def password = delegateExecution.getVariable("emailPassword")
     def from = delegateExecution.getVariable("emailFrom")
     def to = delegateExecution.getVariable("emailTo")
     def subject = delegateExecution.getVariable("emailSubject")
     def text = delegateExecution.getVariable("emailText")
     def files = delegateExecution.getVariable("emailFiles")
     */

    @Test
    void shouldSendMessages(){
        def task = new WriteSmtpMessagesTask()
        def variables = [emailSmtpHost:"smtp.example.net",
                emailSmtpPort:465,
                emailUsername:"email",
                emailPassword:"password",
                emailFrom:"email",
                emailTo:"email",
                emailSubject:"test",
                emailText:"sup?",
                emailFiles:["/Users/kelly/Documents/airgap.png"]
        ]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 8, 2)
        task.execute(delegateExecution)
        assert variables.result.data.emailMessage.subject == "test"
        sleep(10000)
    }

}
