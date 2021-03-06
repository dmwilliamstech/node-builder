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

import node.builder.bpm.ReadImapMessagesTask
import node.builder.bpm.WriteSmtpMessagesTask
import org.junit.Ignore
import org.junit.Test

class ReadImapMessagesTaskTests extends BPMNTaskTestBase{

    /**
     *         def protocol = delegateExecution.getVariable("emailProtocol")
     def host = delegateExecution.getVariable("emailHost")
     def port = delegateExecution.getVariable("emailPort")
     def username = delegateExecution.getVariable("emailUsername")
     def password = delegateExecution.getVariable("emailPassword")
     */

    @Ignore
    @Test
    void shouldGetMessages(){
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

        sleep(5000)

        new File("/tmp/emailAttachment.txt").delete()
        task = new ReadImapMessagesTask()
        variables = [
                emailImapHost:Config.config.get("email.imap.hostname"),
                emailImapPort:Config.config.get("email.imap.port"),
                emailUsername:Config.config.get("email.imap.username"),
                emailPassword:Config.config.get("email.imap.password")]

        delegateExecution = mockDelegateExecutionWithVariables(variables, 5, 2)
        task.execute(delegateExecution)
        assert !variables.result.data.emailNewMessages.empty
        assert (new File("/tmp/emailAttachment.txt")).exists()
    }

}
