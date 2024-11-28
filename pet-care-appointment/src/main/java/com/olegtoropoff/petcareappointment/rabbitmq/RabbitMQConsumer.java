package com.olegtoropoff.petcareappointment.rabbitmq;

import com.olegtoropoff.petcareappointment.email.EmailService;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.service.user.IUserService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {
    private final IUserService userService;
    private final IVerificationTokenService tokenService;
    private final EmailService emailService;
    private final IAppointmentService appointmentService;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;


    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        try {
            String[] parts = message.split(":");
            String eventType = parts[0];
            String data = parts[1];

            switch (eventType) {
                case "RegistrationCompleteEvent":
                    try {
                        sendRegistrationVerificationEmail(data);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(FeedBackMessage.REGISTRATION_EMAIL_FAILED, e);
                    }
                    break;
                case "AppointmentBookedEvent":
                    try {
                        sendAppointmentBookedNotification(data);
                    } catch (Exception e) {
                        throw new RuntimeException(FeedBackMessage.APPOINTMENT_BOOKED_NOTIFICATION_FAILED, e);
                    }
                    break;
                case "AppointmentCanceledEvent":
                    try {
                        sendAppointmentCanceledNotification(data);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(FeedBackMessage.APPOINTMENT_CANCELED_NOTIFICATION_FAILED, e);
                    }
                    break;
                case "AppointmentApprovedEvent":
                    try {
                        sendAppointmentApprovedNotification(data);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(FeedBackMessage.APPOINTMENT_APPROVED_NOTIFICATION_FAILED, e);
                    }
                    break;
                case "AppointmentDeclinedEvent":
                    try {
                        sendAppointmentDeclinedNotification(data);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(FeedBackMessage.APPOINTMENT_DECLINED_NOTIFICATION_FAILED, e);
                    }
                    break;
                case "PasswordResetEvent":
                    try {
                        sendPasswordResetEmail(data);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(FeedBackMessage.PASSWORD_RESET_EMAIL_FAILED, e);
                    }
                    break;
                default:
                    throw new IllegalArgumentException(FeedBackMessage.UNKNOWN_EVENT_TYPE + eventType);
            }
        } catch (Exception e) {
            System.err.println(FeedBackMessage.EVENT_PROCESSING_FAILED + e.getMessage());
        }
    }

    private void sendRegistrationVerificationEmail(String data) throws MessagingException, UnsupportedEncodingException {
        Long userId = Long.parseLong(data);
        User user = userService.findById(userId);
        VerificationToken token = tokenService.findTokenByUserId(userId);
        String vToken = token.getToken();
        String verificationUrl = frontendBaseUrl + "/email-verification?token=" + vToken;

        String subject = "Подтвердите свой адрес электронной почты";
        String senderName = "Doctor Aibolit";
        String mailContent = user.getFirstName() + ", благодарим вас за регистрацию на нашем сайте! Пожалуйста, перейдите по ссылке, чтобы завершить регистрацию. </p>" +
                "<a href=\"" + verificationUrl + "\">Подтвердите свой адрес электронной почты.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";

        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }

    private void sendAppointmentBookedNotification(String data) throws MessagingException, UnsupportedEncodingException {
        Long veterinarianId = Long.parseLong(data);
        Veterinarian veterinarian = (Veterinarian) userService.findById(veterinarianId);

        String subject = "Уведомление о новой записи на прием";
        String senderName = "Doctor Aibolit";
        String mailContent = veterinarian.getFirstName() + ", у вас новая запись на прием! </p>" +
                "<a href=\"" + frontendBaseUrl + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о приеме.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";

        emailService.sendEmail(veterinarian.getEmail(), subject, senderName, mailContent);
    }

    private void sendAppointmentCanceledNotification(String data) throws MessagingException, UnsupportedEncodingException {
        String[] parts = data.split("#");
        Long veterinarianId = Long.parseLong(parts[0]);
        String appointmentNo = parts[1];
        User veterinarian = userService.findById(veterinarianId);

        String subject = "Запись на прием отменена";
        String senderName = "Doctor Aibolit";
        String mailContent = veterinarian.getFirstName() + ", запись на прием № " + appointmentNo + " была отменена клиентом!  </p>" +
                "<a href=\"" + frontendBaseUrl + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о приеме.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";

        emailService.sendEmail(veterinarian.getEmail(), subject, senderName, mailContent);
    }

    private void sendAppointmentApprovedNotification(String data) throws MessagingException, UnsupportedEncodingException {
        Long patientId = Long.parseLong(data);
        User patient = userService.findById(patientId);

        String subject = "Запись на прием подтверждена";
        String senderName = "Doctor Aibolit";
        String mailContent = patient.getFirstName() + ", ваша запись на прием подтверждена! </p>" +
                "<a href=\"" + frontendBaseUrl + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о приеме.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";

        emailService.sendEmail(patient.getEmail(), subject, senderName, mailContent);
    }

    private void sendAppointmentDeclinedNotification(String data) throws MessagingException, UnsupportedEncodingException {
        Long patientId = Long.parseLong(data);
        User patient = userService.findById(patientId);

        String subject = "Запись на прием не подтверждена";
        String senderName = "Doctor Aibolit";
        String mailContent = patient.getFirstName() + ", к сожалению, ваша запись на прием не была подтверждена ветеринаром! Вы можете перенести прием на другую дату.</p>" +
                "<a href=\"" + frontendBaseUrl + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о приеме.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";

        emailService.sendEmail(patient.getEmail(), subject, senderName, mailContent);
    }

    private void sendPasswordResetEmail(String data) throws MessagingException, UnsupportedEncodingException {
        String[] parts = data.split("#");
        Long userId = Long.parseLong(parts[0]);
        String vToken = parts[1];
        User user = userService.findById(userId);

        String resetUrl = frontendBaseUrl + "/reset-password?token=" + vToken;

        String subject = "Запрос на сброс пароля";
        String senderName = "Doctor Aibolit";
        String mailContent = user.getFirstName() + ", вы запросили сброс вашего пароля. Пожалуйста, перейдите по ссылке, чтобы продолжить: </p>" +
                "<a href=\"" + resetUrl + "\">Сброс пароля</a><br/>" +
                "<p>Если вы не запрашивали сброс пароля, пожалуйста, проигнорируйте это письмо.</p>" +
                "<p>С уважением,<br> Doctor Aibolit";

        emailService.sendEmail(user.getEmail(), subject, senderName, mailContent);
    }
}

