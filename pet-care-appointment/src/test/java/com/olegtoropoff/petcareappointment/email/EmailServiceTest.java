package com.olegtoropoff.petcareappointment.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
class EmailServiceTest {

    @Mock
    private EmailProperties emailProperties;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmail_WhenValidInputs_SendsEmail() throws MessagingException, UnsupportedEncodingException {
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String senderName = "Test Sender";
        String mailContent = "<p>This is a test email</p>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(emailProperties.getUsername()).thenReturn("sender@example.com");
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendEmail(to, subject, senderName, mailContent);

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
