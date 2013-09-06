package node.builder.bpm

import grails.converters.JSON
import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate
import org.apache.commons.io.FilenameUtils
import org.joda.time.DateTime

import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.search.FlagTerm

class WriteSmtpMessagesTask implements JavaDelegate{
    void execute(DelegateExecution delegateExecution) throws Exception {
        def host = delegateExecution.getVariable("emailSmtpHost")
        def port = delegateExecution.getVariable("emailSmtpPort")
        def username = delegateExecution.getVariable("emailUsername")
        def password = delegateExecution.getVariable("emailPassword")
        def from = delegateExecution.getVariable("emailFrom")
        def to = delegateExecution.getVariable("emailTo")
        def subject = delegateExecution.getVariable("emailSubject")
        def text = delegateExecution.getVariable("emailText")
        def files = delegateExecution.getVariable("emailFiles")

        def result = delegateExecution.getVariable("result")?: new ProcessResult()

        log.info "Sending new mail message"
        log.info "Connecting to smtp server"

        Properties props = new Properties()
        props.setProperty("mail.smtp.host", host)
        props.setProperty("mail.smtp.port", port.toString())
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        def session = Session.getDefaultInstance(props, null)
        def transport = session.getTransport("smtp")
        transport.connect(host, port, username, password)

        log.info "Creating email message"
        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);
        try {

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));
            message.setSender(new InternetAddress(from))
            message.setSentDate(DateTime.now().toDate())

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // Now set the actual message
            def messageBodyPart = new MimeBodyPart()
            messageBodyPart.setText(text);
            multipart.addBodyPart(messageBodyPart)

            files?.each {file ->
                 addAttachment(multipart, file)
            }

            //set the content
            message.setContent(multipart)
            // Send message
            Address [] addresses = new Address[1]
            addresses[0] = new InternetAddress(to)
            transport.sendMessage(message, addresses);


        } finally {
            if(transport) {
                transport.close()
            }
        }
        log.info "Message sent"
        result.message = "Mail sent!"
        result.data.emailMessage = normalizeMessage(message)
        delegateExecution.setVariable("result", result)
    }

    private void addAttachment(Multipart multipart, String filename)
    {
        DataSource source = new FileDataSource(filename)
        BodyPart messageBodyPart = new MimeBodyPart()
        messageBodyPart.setDataHandler(new DataHandler(source))
        messageBodyPart.setFileName(FilenameUtils.getName(filename))
        multipart.addBodyPart(messageBodyPart)
    }

    private Map normalizeMessage(Message message){
        def map = [:]

        message.properties.each { entry ->
            switch(entry.key){
                case "class":
                case "allHeaderLines":
                case "content":
                case "allHeaders":
                case "dataHandler":
                case "inputStream":
                case "flags":
                    break
                default:
                    map[entry.key] = entry.value
                    break
            }

        }
        return map
    }
}
