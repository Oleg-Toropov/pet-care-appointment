package com.olegtoropoff.petcareappointment.event.listener;

import com.olegtoropoff.petcareappointment.email.EmailService;
import com.olegtoropoff.petcareappointment.event.*;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener implements ApplicationListener<ApplicationEvent> {
    private final EmailService emailService;
    private final IVerificationTokenService tokenService;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        Object source = event.getSource();
        switch (event.getClass().getSimpleName()) {

            case "RegistrationCompleteEvent":
                if (source instanceof User) {
                    handleSendRegistrationVerificationEmail((RegistrationCompleteEvent) event);
                }
                break;

            case "AppointmentBookedEvent":
                if (source instanceof Appointment) {
                    try {
                        handleAppointmentBookedNotification((AppointmentBookedEvent) event);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;

            case "AppointmentApprovedEvent":
                if (source instanceof Appointment) {
                    try {
                        handleAppointmentApprovedNotification((AppointmentApprovedEvent) event);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;

            case "AppointmentDeclinedEvent":
                if (source instanceof Appointment) {
                    try {
                        handleAppointmentDeclinedNotification((AppointmentDeclinedEvent) event);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;

            case "PasswordResetEvent":
                PasswordResetEvent passwordResetEvent = (PasswordResetEvent) event;
                handlePasswordResetRequest(passwordResetEvent);
                break;

            default:
                break;
        }
    }

    /*=================== Start user registration email verification ============================*/
    private void handleSendRegistrationVerificationEmail(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String vToken = UUID.randomUUID().toString();
        tokenService.saveVerificationTokenForUser(vToken, user);
        String verificationUrl = frontendBaseUrl + "/email-verification?token=" + vToken;
        try {
            sendRegistrationVerificationEmail(user, verificationUrl);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendRegistrationVerificationEmail(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Подтвердите свой адрес электронной почты";
        String senderName = "Doctor Aibolit";
        String mailContent = user.getFirstName() + ", благодарим вас за регистрацию на нашем сайте! Пожалуйста, перейдите по ссылке, чтобы завершить регистрацию. </p>" +
                "<a href=\"" + url + "\">Подтвердите свой адрес электронной почты.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }
    /*=================== End user registration email verification ============================*/

    /*======================== Start New Appointment booked notifications ===================================================*/
    private void handleAppointmentBookedNotification(AppointmentBookedEvent event) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = event.getAppointment();
        User veterinarian = appointment.getVeterinarian();
        sendAppointmentBookedNotification(veterinarian, frontendBaseUrl);
    }

    private void sendAppointmentBookedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Уведомление о новой записи на прием";
        String senderName = "Doctor Aibolit";
        String mailContent = user.getFirstName() + ", у вас новая запись на прием! </p>" +
                "<a href=\"" + url + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о прием.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }
    /*======================== End New Appointment Booked notifications ===================================================*/

    /*======================== Start Approve Appointment notifications ===================================================*/
    private void handleAppointmentApprovedNotification(AppointmentApprovedEvent event) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = event.getAppointment();
        User patient = appointment.getPatient();
        sendAppointmentApprovedNotification(patient, frontendBaseUrl);
    }

    private void sendAppointmentApprovedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Запись на прием подтверждена";
        String senderName = "Doctor Aibolit";
        String mailContent = user.getFirstName() + ", ваша запись на прием подтверждена! </p>" +
                "<a href=\"" + url + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о прием.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }
    /*======================== End Approve Appointment notifications ===================================================*/

    /*======================== Start Decline Appointment notifications ===================================================*/
    private void handleAppointmentDeclinedNotification(AppointmentDeclinedEvent event) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = event.getAppointment();
        User patient = appointment.getPatient();
        sendAppointmentDeclinedNotification(patient, frontendBaseUrl);
    }

    private void sendAppointmentDeclinedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Запись на прием не подтверждена";
        String senderName = "Doctor Aibolit";
        String mailContent = user.getFirstName() + ", к сожалению, ваша запись на прием не была подтверждена ветеринаром! Вы можете перенести прием на другую дату.</p>" +
                "<a href=\"" + url + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о приеме.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }
    /*======================== End Decline Appointment notifications ===================================================*/

    /*======================== Start password reset related notifications ===================================================*/
    private void handlePasswordResetRequest(PasswordResetEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        tokenService.saveVerificationTokenForUser(token, user);
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + token;
        try {
            sendPasswordResetEmail(user, resetUrl);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(FeedBackMessage.PASSWORD_RESET_EMAIL_FAILED, e);
        }
    }

    private void sendPasswordResetEmail(User user, String resetUrl) throws MessagingException, UnsupportedEncodingException {
        String subject = "Запрос на сброс пароля";
        String senderName = "Doctor Aibolit";
        String mailContent = user.getFirstName() + ", вы запросили сброс вашего пароля. Пожалуйста, перейдите по ссылке, чтобы продолжить: </p>" +
                "<a href=\"" + resetUrl + "\">Сброс пароля</a><br/>" +
                "<p>Если вы не запрашивали сброс пароля, пожалуйста, проигнорируйте это письмо.</p>" +
                "<p>С уважением,<br> Doctor Aibolit";
        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }
    /*======================== End password reset related notifications ===================================================*/
}
