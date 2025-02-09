package com.olegtoropoff.petcareappointment.rabbitmq;

import com.olegtoropoff.petcareappointment.email.EmailService;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.service.user.IUserService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * RabbitMQ message consumer for handling various event-driven tasks.
 * <p>
 * This class listens to a specified RabbitMQ queue and processes incoming messages,
 * performing actions such as sending emails for registration verification, appointment
 * notifications, and password resets.
 */
@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    /**
     * Service for user-related operations.
     */
    private final IUserService userService;

    /**
     * Service for verification token management.
     */
    private final IVerificationTokenService tokenService;

    /**
     * Service for email operations.
     */
    private final EmailService emailService;

    /**
     * Base URL for the frontend application.
     */
    @Value("${frontend.base.url}")
    private String frontendBaseUrl;


    /**
     * Processes incoming messages from the RabbitMQ queue.
     * <p>
     * The method parses the message and determines the action to perform based on the event type.
     * Supported events include:
     * <ul>
     *     <li>RegistrationCompleteEvent</li>
     *     <li>AppointmentBookedEvent</li>
     *     <li>AppointmentCanceledEvent</li>
     *     <li>AppointmentApprovedEvent</li>
     *     <li>AppointmentDeclinedEvent</li>
     *     <li>PasswordResetEvent</li>
     * </ul>
     *
     * @param message The message received from the queue.
     */
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

    /**
     * Sends a registration verification email to the user.
     *
     * @param data The ID of the user to whom the email will be sent.
     * @throws MessagingException If an error occurs while sending the email.
     * @throws UnsupportedEncodingException If the email encoding is unsupported.
     */
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
        // FOR ADMIN
        emailService.sendEmail("toropovoleg1987@gmail.com",
                "Регистрация нового пользователя на сайте \"Doctor Aibolit\"",
                "Doctor Aibolit", "Электронная почта нового пользователя: " +
                                  user.getEmail() + " тип пользователя: " + user.getUserType());
    }

    /**
     * Sends a notification to the veterinarian about a booked appointment.
     *
     * @param data The ID of the veterinarian.
     * @throws MessagingException If an error occurs while sending the email.
     * @throws UnsupportedEncodingException If the email encoding is unsupported.
     */
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

    /**
     * Sends a notification to the veterinarian about a canceled appointment.
     *
     * @param data The ID of the veterinarian and appointment number.
     * @throws MessagingException If an error occurs while sending the email.
     * @throws UnsupportedEncodingException If the email encoding is unsupported.
     */
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

    /**
     * Sends a notification to the patient about an approved appointment.
     *
     * @param data The ID of the patient.
     * @throws MessagingException If an error occurs while sending the email.
     * @throws UnsupportedEncodingException If the email encoding is unsupported.
     */
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

    /**
     * Sends a notification to the patient about a declined appointment.
     *
     * @param data The ID of the patient.
     * @throws MessagingException If an error occurs while sending the email.
     * @throws UnsupportedEncodingException If the email encoding is unsupported.
     */
    private void sendAppointmentDeclinedNotification(String data) throws MessagingException, UnsupportedEncodingException {
        Long patientId = Long.parseLong(data);
        User patient = userService.findById(patientId);

        String subject = "Запись на прием не подтверждена";
        String senderName = "Doctor Aibolit";
        String mailContent = patient.getFirstName() + ", к сожалению, ваша запись на прием не была подтверждена ветеринаром! Вы можете записаться на прием на другую дату или к другому ветеринару.</p>" +
                "<a href=\"" + frontendBaseUrl + "\">Посетите сайт клиники, чтобы ознакомиться с подробной информацией о приеме.</a> <br/>" +
                "<p>С уважением,<br> Doctor Aibolit";

        emailService.sendEmail(patient.getEmail(), subject, senderName, mailContent);
    }

    /**
     * Sends a password reset email to the user.
     *
     * @param data The ID of the user and the verification token.
     * @throws MessagingException If an error occurs while sending the email.
     * @throws UnsupportedEncodingException If the email encoding is unsupported.
     */
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

