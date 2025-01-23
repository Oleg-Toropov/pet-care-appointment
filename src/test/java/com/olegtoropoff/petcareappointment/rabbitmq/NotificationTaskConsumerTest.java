package com.olegtoropoff.petcareappointment.rabbitmq;

import com.olegtoropoff.petcareappointment.email.EmailService;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.service.user.IUserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@Tag("unit")
class NotificationTaskConsumerTest {

    @InjectMocks
    private RabbitMQConsumer rabbitMQConsumer;

    @Mock
    private IUserService userService;

    @Mock
    private IVerificationTokenService tokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private IAppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void receiveMessage_WhenRegistrationCompleteEvent_SendsVerificationEmail() throws MessagingException, UnsupportedEncodingException {
        String message = "RegistrationCompleteEvent:1";
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setEmail("john@example.com");
        VerificationToken token = new VerificationToken();
        token.setToken("sampleToken");

        when(userService.findById(1L)).thenReturn(user);
        when(tokenService.findTokenByUserId(1L)).thenReturn(token);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        rabbitMQConsumer.receiveMessage(message);

        verify(userService, times(1)).findById(1L);
        verify(tokenService, times(1)).findTokenByUserId(1L);
        verify(emailService, times(1)).sendEmail(eq("john@example.com"), anyString(), anyString(), contains("sampleToken"));
    }

    @Test
    void receiveMessage_WhenAppointmentBookedEvent_SendsNotificationEmail() throws MessagingException, UnsupportedEncodingException {
        String message = "AppointmentBookedEvent:2";
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(2L);
        veterinarian.setFirstName("Smith");
        veterinarian.setEmail("john@example.com");

        when(userService.findById(2L)).thenReturn(veterinarian);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        rabbitMQConsumer.receiveMessage(message);

        verify(userService, times(1)).findById(2L);
        verify(emailService, times(1)).sendEmail(eq("john@example.com"), anyString(), anyString(), contains("у вас новая запись на прием"));
    }

    @Test
    void receiveMessage_WhenUnknownEventType_ThrowsException() {
        String message = "UnknownEvent:1";

        rabbitMQConsumer.receiveMessage(message);

        verifyNoInteractions(userService, tokenService, emailService, appointmentService);
    }

    @Test
    void receiveMessage_WhenInvalidMessage_LogsError() {
        String message = "InvalidMessage";

        rabbitMQConsumer.receiveMessage(message);

        verifyNoInteractions(userService, tokenService, emailService, appointmentService);
    }

    @Test
    void receiveMessage_WhenAppointmentCanceledEvent_SendsNotificationEmail() throws MessagingException, UnsupportedEncodingException {
        String message = "AppointmentCanceledEvent:2#12345";
        User veterinarian = new User();
        veterinarian.setId(2L);
        veterinarian.setFirstName("Smith");
        veterinarian.setEmail("smith@example.com");

        when(userService.findById(2L)).thenReturn(veterinarian);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        rabbitMQConsumer.receiveMessage(message);

        verify(userService, times(1)).findById(2L);
        verify(emailService, times(1)).sendEmail(eq("smith@example.com"), anyString(), anyString(), contains("была отменена клиентом"));
    }

    @Test
    void receiveMessage_WhenAppointmentApprovedEvent_SendsNotificationEmail() throws MessagingException, UnsupportedEncodingException {
        String message = "AppointmentApprovedEvent:3";
        User patient = new User();
        patient.setId(3L);
        patient.setFirstName("Jane");
        patient.setEmail("jane@example.com");

        when(userService.findById(3L)).thenReturn(patient);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        rabbitMQConsumer.receiveMessage(message);

        verify(userService, times(1)).findById(3L);
        verify(emailService, times(1)).sendEmail(eq("jane@example.com"), anyString(), anyString(), contains("ваша запись на прием подтверждена"));
    }

    @Test
    void receiveMessage_WhenAppointmentDeclinedEvent_SendsNotificationEmail() throws MessagingException, UnsupportedEncodingException {
        String message = "AppointmentDeclinedEvent:4";
        User patient = new User();
        patient.setId(4L);
        patient.setFirstName("Anna");
        patient.setEmail("anna@example.com");

        when(userService.findById(4L)).thenReturn(patient);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        rabbitMQConsumer.receiveMessage(message);

        verify(userService, times(1)).findById(4L);
        verify(emailService, times(1)).sendEmail(eq("anna@example.com"), anyString(), anyString(), contains("ваша запись на прием не была подтверждена"));
    }

    @Test
    void receiveMessage_WhenPasswordResetEvent_SendsResetEmail() throws MessagingException, UnsupportedEncodingException {
        String message = "PasswordResetEvent:5#resetToken";
        User user = new User();
        user.setId(5L);
        user.setFirstName("Tom");
        user.setEmail("tom@example.com");

        when(userService.findById(5L)).thenReturn(user);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        rabbitMQConsumer.receiveMessage(message);

        verify(userService, times(1)).findById(5L);
        verify(emailService, times(1)).sendEmail(eq("tom@example.com"), anyString(), anyString(), contains("Сброс пароля"));
    }
}
