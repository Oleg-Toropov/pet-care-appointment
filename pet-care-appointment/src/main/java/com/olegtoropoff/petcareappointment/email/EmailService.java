package com.olegtoropoff.petcareappointment.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Service for sending email notifications.
 * <p>
 * This service provides functionality to send emails using SMTP.
 * Emails are sent with customizable recipients, subjects, senders, and content.
 */
@Service
public class EmailService {

    private final EmailProperties emailProperties;
    private final JavaMailSender mailSender;

    /**
     * Constructs the EmailService with the necessary dependencies.
     *
     * @param emailProperties the email configuration properties
     * @param mailSender the JavaMailSender instance for sending emails
     */
    public EmailService(EmailProperties emailProperties, JavaMailSender mailSender) {
        this.emailProperties = emailProperties;
        this.mailSender = mailSender;
    }

    /**
     * Sends an email with the specified parameters.
     *
     * @param to          the recipient's email address
     * @param subject     the subject of the email
     * @param senderName  the name of the sender
     * @param mailContent the content of the email (supports HTML)
     * @throws MessagingException if there is an issue creating or sending the email
     * @throws UnsupportedEncodingException if the sender's name cannot be encoded
     */
    public void sendEmail(String to, String subject, String senderName, String mailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);

        messageHelper.setFrom(emailProperties.getUsername(), senderName);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }
}
