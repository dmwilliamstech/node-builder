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
        new File("target/emailAttachment.txt").write("Test test 1 2")
        def task = new WriteSmtpMessagesTask()
        def variables = [emailSmtpHost: Config.config.get("email.smtp.hostname"),
                emailSmtpPort: Config.config.get("email.smtp.port"),
                emailUsername: Config.config.get("email.smtp.username"),
                emailPassword: Config.config.get("email.smtp.password"),
                emailFrom: Config.config.get("email.smtp.from"),
                emailTo: Config.config.get("email.smtp.from"),
                emailSubject:"test",
                emailText:"this is a test\n",
                emailFiles:["target/emailAttachment.txt"]
        ]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 10, 2)
        task.execute(delegateExecution)
        assert variables.result.data.emailMessage.subject == "test"
    }

}
