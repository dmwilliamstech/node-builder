package node.builder

import node.builder.bpm.ReadImapMessagesTask
import org.junit.Test

class ReadImapMessagesTaskTests extends BPMNTaskTestBase{

    /**
     *         def protocol = delegateExecution.getVariable("emailProtocol")
     def host = delegateExecution.getVariable("emailHost")
     def port = delegateExecution.getVariable("emailPort")
     def username = delegateExecution.getVariable("emailUsername")
     def password = delegateExecution.getVariable("emailPassword")
     */

    @Test
    void shouldGetMessages(){
        def task = new ReadImapMessagesTask()
        def variables = [emailImapHost:"imap.example.net", emailImapPort:143, emailUsername:"email", emailPassword:"password"]
        def delegateExecution = mockDelegateExecutionWithVariables(variables, 3, 2)
        task.execute(delegateExecution)
        assert !variables.result.data.emailNewMessages.empty
        assert !(new File("/tmp/airgap.png")).exists()
    }

}
