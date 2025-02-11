package com.olegtoropoff.petcareappointment.utils;

/**
 * A utility class containing predefined feedback messages used throughout the application.
 * <p>
 * This class centralizes all feedback messages for APIs, exceptions, and operations,
 * enabling easier maintenance and localization.
 * <p>
 * Messages are grouped by feature or domain, such as User API, Password API, Appointment API, etc.
 * Each group is marked by a descriptive comment for better organization.
 */
public class FeedBackMessage {
    /*======================== Start User API =====================================*/
    /**
     * Message indicating successful creation of a user account.
     * Directs the user to complete registration via the provided email link.
     */
    public static final String CREATE_USER_SUCCESS = "Учетная запись пользователя успешно создана для завершения регистрации перейдите по ссылке которая была отправлена на указанный при регистрации электронный адрес";

    /**
     * Message indicating successful deletion of a user account.
     */
    public static final String DELETE_USER_SUCCESS = "Учетная запись пользователя успешно удалена";

    /**
     * Message indicating that a user with the specified email already exists.
     */
    public static final String USER_ALREADY_EXISTS = "Пользователь с таким email уже существует";

    /**
     * Message indicating successful update of user information.
     */
    public static final String USER_UPDATE_SUCCESS = "Пользователь успешно обновлен";

    /**
     * Message indicating that a user has been found.
     */
    public static final String USER_FOUND = "Пользователь найден";

    /**
     * Message indicating that multiple users have been found.
     */
    public static final String USERS_FOUND = "Пользователи найдены";

    /**
     * Message indicating that a user was not found.
     */
    public static final String USER_NOT_FOUND = "Извините, пользователь не найден";

    /**
     * Message indicating that a user with the specified email was not found.
     * Includes the provided email in the message.
     */
    public static final String USER_NOT_FOUND_WITH_EMAIL = "Извините, пользователь с электронной почтой: '%s' не найден";

    /**
     * Message indicating successful locking of a user account.
     */
    public static final String LOCKED_ACCOUNT_SUCCESS = "Учетная запись успешно заблокирована";

    /**
     * Message indicating successful unlocking of a user account.
     */
    public static final String UNLOCKED_ACCOUNT_SUCCESS = "Учетная запись успешно разблокирована";

    /**
     * Message indicating that no veterinarian was found with the specified specialization.
     * Includes the specialization in the message.
     */
    public static final String SPECIALIZATION_NOT_FOUND = "В системе не найден ветеринар со специализацией '%s'";

    /**
     * Message indicating that available times for an appointment have been successfully found.
     */
    public static final String AVAILABLE_TIME_FOR_APPOINTMENT_FOUND = "Доступное время для приема успешно найдено";

    /**
     * Message indicating an invalid name format, suggesting the user check the input.
     */
    public static final String INVALID_NAME_FORMAT = "Упс! Кажется, в имени или фамилии ошибка. Проверьте, что данные введены правильно";
    /*======================== End User API =====================================*/

    /*======================== Start Password API =====================================*/
    /**
     * Message indicating successful password change.
     * Advises the user that the form can now be closed.
     */
    public static final String PASSWORD_CHANGE_SUCCESS = "Пароль успешно изменен, теперь можно закрыть форму.";

    /**
     * Message indicating that a password reset email has been sent successfully.
     * Directs the user to check their email for further instructions.
     */
    public static final String PASSWORD_RESET_EMAIL_SENT = "Ссылка отправлена на вашу электронную почту, пожалуйста, проверьте ее для завершения запроса на смену пароля.";

    /**
     * Message indicating that the token or password is missing.
     */
    public static final String MISSING_PASSWORD = "Отсутствует токен или пароль";

    /**
     * Message indicating that the provided password reset token is invalid.
     */
    public static final String INVALID_RESET_TOKEN = "Недействительный токен сброса пароля";

    /**
     * Message advising the user to enter an email associated with their account.
     */
    public static final String INVALID_EMAIL = "Пожалуйста, введите email, связанный с вашей учетной записью.";

    /**
     * Message indicating successful password reset.
     */
    public static final String PASSWORD_RESET_SUCCESS = "Ваш пароль успешно сброшен!";

    /**
     * Message indicating failure to send the password reset email.
     */
    public static final String PASSWORD_RESET_EMAIL_FAILED = "Не удалось отправить электронное письмо для сброса пароля";
    /*======================== End Password API =====================================*/

    /*======================== Start Appointment API =====================================*/
    /**
     * Message indicating that the appointment was successfully updated.
     */
    public static final String APPOINTMENT_UPDATE_SUCCESS = "Запись успешно обновлена";

    /**
     * Message indicating that the appointment was successfully approved.
     */
    public static final String APPOINTMENT_APPROVED_SUCCESS = "Запись успешно подтверждена";

    /**
     * Message indicating that the appointment was declined.
     */
    public static final String APPOINTMENT_DECLINED_SUCCESS = "Запись отклонена";

    /**
     * Message indicating that the appointment was cancelled.
     */
    public static final String APPOINTMENT_CANCELLED_SUCCESS = "Запись отменена";

    /**
     * Message indicating that the appointment was successfully deleted.
     */
    public static final String APPOINTMENT_DELETE_SUCCESS = "Запись успешно удалена";

    /**
     * Message indicating that the appointment was successfully booked.
     */
    public static final String APPOINTMENT_BOOKED_SUCCESS = "Запись успешно зарегистрирована";

    /**
     * Message indicating that a specific appointment was found.
     */
    public static final String APPOINTMENT_FOUND = "Запись найдена";

    /**
     * Message indicating that multiple appointments were found.
     */
    public static final String APPOINTMENTS_FOUND = "Записи найдены";

    /**
     * Message indicating that the specified appointment could not be found.
     */
    public static final String APPOINTMENT_NOT_FOUND = "Запись не найдена";

    /**
     * Message indicating that updating or cancelling the appointment is not allowed.
     */
    public static final String APPOINTMENT_UPDATE_NOT_ALLOWED = "Невозможно обновить или отменить запись";

    /**
     * Message indicating that veterinarians are not allowed to book appointments for themselves.
     */
    public static final String VET_APPOINTMENT_NOT_ALLOWED = "Запись на прием для ветеринаров недоступна";

    /**
     * Message indicating that the user has too many active appointments.
     * Limits booking a new appointment until one of the active appointments is completed.
     */
    public static final String TOO_MANY_ACTIVE_APPOINTMENTS = "У вас есть 2 предстоящих приема. Вы не можете записаться на новый прием, пока один из них не завершится.";
    /*======================== End Appointment API =====================================*/

    /*======================== Start Pet API =====================================*/
    /**
     * Message indicating that a pet was successfully added to the appointment.
     */
    public static final String PET_ADDED_SUCCESS = "Питомец успешно добавлен к записи";

    /**
     * Message indicating that the pet's information was successfully updated.
     */
    public static final String PET_UPDATE_SUCCESS = "Информация о питомце успешно обновлена";

    /**
     * Message indicating that a pet was successfully deleted.
     */
    public static final String PET_DELETE_SUCCESS = "Питомец успешно удален";

    /**
     * Message indicating that one or more pets were found.
     */
    public static final String PET_FOUND = "Питомец(ы) найден(ы)";

    /**
     * Message indicating that the specified pet could not be found.
     */
    public static final String PET_NOT_FOUND = "Питомец не найден";

    /**
     * Message indicating that deleting the last pet associated with an appointment is not allowed.
     * The user is prompted to cancel the appointment first if necessary.
     */
    public static final String NOT_ALLOWED_TO_DELETE_LAST_PET = "Единственный питомец не может быть удален, если необходимо отмените прием";
    /*======================== End Pet API =====================================*/

    /*======================== Start Review API =====================================*/
    /**
     * Message indicating that leaving a review is not allowed.
     * Only patients with a completed appointment with the veterinarian can leave a review.
     */
    public static final String REVIEW_NOT_ALLOWED = "Извините, оставить отзыв могут только пациенты, у которых была завершенная запись с этим ветеринаром";

    /**
     * Message indicating that the user has already left a review for this veterinarian.
     * The user is prompted to delete the previous review to write a new one.
     */
    public static final String ALREADY_REVIEWED = "Вы уже оставили отзыв этому ветеринару, вы можете удалить предыдущий отзыв и написать новый";

    /**
     * Message indicating that veterinarians are not allowed to leave reviews for themselves.
     */
    public static final String CANNOT_REVIEW = "Ветеринары не могут оставлять отзывы о себе";

    /**
     * Message indicating that the specified veterinarian or patient could not be found.
     */
    public static final String VET_OR_PATIENT_NOT_FOUND = "Ветеринар или пациент не найдены";

    /**
     * Message indicating that no veterinarians are available for the selected specialization,
     * date, or time.
     */
    public static final String NO_VETS_AVAILABLE = "По выбранной специальности на указанную дату и время нет доступных ветеринаров";

    /**
     * Message indicating that the review was successfully submitted.
     */
    public static final String REVIEW_SUBMIT_SUCCESS = "Отзыв успешно отправлен";

    /**
     * Message indicating that the review was successfully deleted.
     */
    public static final String REVIEW_DELETE_SUCCESS = "Отзыв успешно удален";

    /**
     * Message indicating that no reviews were found.
     */
    public static final String REVIEW_NOT_FOUND = "Отзыв(ы) не найден(ы)";
    /*======================== End Review API =====================================*/

    /*======================== Start authentication feedback =====================================*/
    /**
     * Message indicating that all fields must be filled in.
     */
    public static final String EMPTY_PASSWORD = "Все поля обязательны для заполнения";

    /**
     * Message indicating that the current password provided is incorrect.
     */
    public static final String CURRENT_PASSWORD_WRONG = "Текущий пароль указан неверно";

    /**
     * Message indicating that the new password must differ from the current password.
     */
    public static final String NEW_PASSWORD_MUST_DIFFER = "Новый пароль должен отличаться от текущего";

    /**
     * Message indicating that the password confirmation does not match the new password.
     */
    public static final String PASSWORD_MISMATCH = "Подтверждение пароля не совпадает";

    /**
     * Message indicating successful authentication.
     */
    public static final String AUTHENTICATION_SUCCESS = "Аутентификация успешна";

    /**
     * Message indicating that the user's account is disabled.
     * The user is prompted to complete the registration process or contact support.
     */
    public static final String ACCOUNT_DISABLED = "Извините, ваша учетная запись отключена, вам необходимо завершить регистрацию: перейдите по ссылке, которая была отправлена на ваш электронный адрес или обратитесь в службу поддержки";

    /**
     * Message indicating invalid username or password.
     */
    public static final Object INVALID_PASSWORD = "Неверное имя пользователя или пароль";
    /*======================== End authentication feedback =====================================*/

    /*======================== Start Token API =====================================*/
    /**
     * Status message indicating that the token is invalid.
     */
    public static final String INVALID_TOKEN = "INVALID";

    /**
     * Status message indicating that the token is already verified.
     */
    public static final String TOKEN_ALREADY_VERIFIED = "VERIFIED";

    /**
     * Status message indicating that the token has expired.
     */
    public static final String EXPIRED_TOKEN = "EXPIRED";

    /**
     * Status message indicating that the token is valid.
     */
    public static final String VALID_TOKEN = "VALID";

    /**
     * Message indicating an error during token validation.
     */
    public static final String TOKEN_VALIDATION_ERROR = "Ошибка проверки токена";

    /**
     * Message indicating that the verification token was successfully saved.
     */
    public static final String TOKEN_SAVED_SUCCESS = "Токен подтверждения успешно сохранен";

    /**
     * Message indicating that the user's token was successfully deleted.
     */
    public static final String TOKEN_DELETE_SUCCESS = "Токен пользователя успешно удален";

    /**
     * Message indicating that the provided verification token is invalid.
     */
    public static final String INVALID_VERIFICATION_TOKEN = "Недействительный токен подтверждения";

    /**
     * Message indicating that a new verification token has been sent to the user's email.
     */
    public static final String NEW_VERIFICATION_TOKEN_SENT = "Новая ссылка для подтверждения отправлена на вашу почту. Пожалуйста, перейдите по ней для завершения регистрации.";

    /**
     * Message indicating that the provided JWT token is either invalid or expired.
     */
    public static final String INVALID_OR_EXPIRED_JWT = "Недействительный или просроченный токен JWT";
    /*======================== End Token API =====================================*/

    /*======================== Start Role API =====================================*/
    /**
     * Message indicating that the specified role was not found.
     */
    public static final String ROLE_NOT_FOUND = "Роль не найдена";
    /*======================== End Role API =====================================*/

    /*======================== Start Photo API =====================================*/
    /**
     * Message indicating that the photo was successfully updated.
     */
    public static final String PHOTO_UPDATE_SUCCESS = "Фотография успешно обновлена";

    /**
     * Message indicating that the photo was successfully removed.
     */
    public static final String PHOTO_REMOVE_SUCCESS = "Фотография успешно удалена";
    /*======================== End Photo API =====================================*/

    /*======================== Start General feedback =====================================*/
    /**
     * General message indicating success.
     */
    public static final String SUCCESS = "Успешно";

    /**
     * General message indicating that the resource was found.
     */
    public static final String RESOURCE_FOUND = "Ресурс найден";

    /**
     * General message indicating that the sender or recipient was not found.
     */
    public static final String SENDER_RECIPIENT_NOT_FOUND = "Отправитель или получатель не найдены";

    /**
     * General message indicating that an error occurred.
     */
    public static final String ERROR = "Произошла ошибка";

    /**
     * General message indicating that the specified resource was not found.
     */
    public static final String RESOURCE_NOT_FOUND = "Ресурс не найден";

    /**
     * General message indicating that the requested operation is not allowed.
     */
    public static final String OPERATION_NOT_ALLOWED = "Операция не разрешена";
    /*======================== End general feedback =====================================*/

    /*======================== Start VetBiography API =====================================*/
    /**
     * Message indicating that the veterinarian's information is currently unavailable.
     */
    public static final String VETERINARIAN_INFO_NOT_AVAILABLE = "Информация о ветеринаре пока отсутствует, но вскоре появится!";

    /**
     * Message indicating that the specified veterinarian was not found.
     */
    public static final String VETERINARIAN_NOT_FOUND = "Ветеринар не найден";

    /**
     * Message indicating that the biography was found.
     */
    public static final String BIOGRAPHY_FOUND = "Биография найдена";

    /**
     * Message indicating that the biography was not found.
     */
    public static final String BIOGRAPHY_NOT_FOUND = "Биография не найдена";

    /**
     * Message indicating that the biography was successfully saved.
     */
    public static final String BIOGRAPHY_SAVED_SUCCESS = "Биография успешно сохранена";

    /**
     * Message indicating that the biography was successfully updated.
     */
    public static final String BIOGRAPHY_UPDATED_SUCCESS = "Биография успешно обновлена";

    /**
     * Message indicating that the biography was successfully deleted.
     */
    public static final String BIOGRAPHY_DELETED_SUCCESS = "Биография успешно удалена";
    /*======================== End VetBiography API =====================================*/

    /*======================== Start Validator =====================================*/
    /**
     * Message indicating an invalid password format.
     */
    public static final String INVALID_PASSWORD_FORMAT = "Пароль должен быть не менее 8 символов и содержать буквы и цифры латинского алфавита!";

    /**
     * Message indicating an invalid email format.
     */
    public static final String INVALID_EMAIL_FORMAT = "Упс! Кажется, в адресе электронной почты ошибка. Проверьте, что ваш email введён правильно.";

    /**
     * Message indicating an invalid phone number format.
     */
    public static final String INVALID_PHONE_FORMAT = "Упс! Кажется, в номере телефона ошибка. Проверьте, что номер телефона введён правильно.";

    /**
     * Message indicating an invalid clinic address format.
     */
    public static final String INVALID_ADDRESS_FORMAT = "Упс! Кажется, в адресе места проведения приема ошибка. Проверьте, что адрес введён правильно.";
    /*======================== End Validator =====================================*/

    /*======================== Start RabbitMQConsumer =====================================*/
    /**
     * Message indicating that the registration confirmation email could not be sent.
     */
    public static final String REGISTRATION_EMAIL_FAILED = "Не удалось отправить письмо для подтверждения регистрации";

    /**
     * Message indicating that the notification for a booked appointment could not be sent.
     */
    public static final String APPOINTMENT_BOOKED_NOTIFICATION_FAILED = "Не удалось отправить уведомление о записи на приём";

    /**
     * Message indicating that the notification for a canceled appointment could not be sent.
     */
    public static final String APPOINTMENT_CANCELED_NOTIFICATION_FAILED = "Не удалось отправить уведомление об отмене записи на приём";

    /**
     * Message indicating that the notification for an approved appointment could not be sent.
     */
    public static final String APPOINTMENT_APPROVED_NOTIFICATION_FAILED = "Не удалось отправить уведомление о подтверждении записи на приём";

    /**
     * Message indicating that the notification for a declined appointment could not be sent.
     */
    public static final String APPOINTMENT_DECLINED_NOTIFICATION_FAILED = "Не удалось отправить уведомление об отклонении записи на приём";

    /**
     * Message indicating an unknown event type.
     */
    public static final String UNKNOWN_EVENT_TYPE = "Неизвестный тип события: ";

    /**
     * Message indicating that the event message processing failed.
     */
    public static final String EVENT_PROCESSING_FAILED = "Не удалось обработать сообщение: ";
    /*======================== End RabbitMQConsumer =====================================*/
}
