package node.builder.bpm

import node.builder.Retryable
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate

import javax.mail.BodyPart
import javax.mail.Flags
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.search.FlagTerm


class ReadImapMessagesTask extends Retryable implements JavaDelegate{
    void execute(DelegateExecution delegateExecution) throws Exception {
        def host = delegateExecution.getVariable("emailImapHost")
        def port = delegateExecution.getVariable("emailImapPort")
        def username = delegateExecution.getVariable("emailUsername")
        def password = delegateExecution.getVariable("emailPassword")
        def result

        log.info "Checking for new mail messages"

        Properties props = new Properties()
        props.setProperty("mail.store.protocol", "imap")
        props.setProperty("mail.imap.host", host)
        props.setProperty("mail.imap.port", port.toString())
        def session = Session.getDefaultInstance(props, null)
        def store = session.getStore("imap")
        def inbox
        def newMessages = []

        try {
            retry({log.warn "Connected to IMAP server, failed retrying"}, java.io.IOException, 5 ){
                store.connect(host, username, password)
            }

            log.info "Connected to IMAP server"
            inbox = store.getFolder("INBOX")
            if(!inbox.isOpen()){
                inbox.open(Folder.READ_WRITE)
            }
            log.info "Openned INBOX for reading"
            def messages = inbox.search(
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false))

            messages.each { message ->
                log.info "Found message ${message.subject}"
                def newMessage = parseMessage(message)
                newMessages.add(newMessage)
                message.setFlag(Flags.Flag.SEEN, true)
                log.info "Marked message as read"
            }
        } finally {
            if(inbox) {
                inbox.close(true)
            }
            store.close()
        }
        log.info "Closed connection to IMAP"
        result = new ProcessResult(newMessages.empty? "No new messages found":"${newMessages.size()} new message(s) found", [emailMessageRecieved: !newMessages.empty, emailNewMessages: newMessages])
        delegateExecution.setVariable("result", result)
    }

    def parseMessage(Message message){
        def newMessage = [:]
        newMessage.subject = message.getSubject()
        newMessage.to = message.getRecipients(Message.RecipientType.TO)
        newMessage.cc = message.getRecipients(Message.RecipientType.CC)
        newMessage.bcc = message.getRecipients(Message.RecipientType.BCC)
        newMessage.from = message.getFrom()
        newMessage.content = message.getContent()
        newMessage.sentDate = message.getSentDate()
        newMessage.headers = message.getAllHeaders()

        def attachments = []
        def files = []
        Multipart multipart = message.getContent()
        (0..multipart.count-1).each {
            BodyPart bodyPart = multipart.getBodyPart(it)
            if(bodyPart.fileName){
                def file = new File("/tmp/${bodyPart.getFileName()}")
                file << bodyPart.getInputStream().text.bytes
                attachments.add([path: "/tmp/${bodyPart.fileName}", name: bodyPart.fileName])
                files.add(file.path)
            }
        }
        newMessage.attachments = attachments
        newMessage.files = files
        return newMessage
    }
}
