package com.olegtoropoff.petcareappointment.utils;

public class FeedBackMessage {
    /*======================== Start User API =====================================*/
    public static final String CREATE_USER_SUCCESS = "Учетная запись пользователя успешно создана для завершения регистрации перейдите по ссылке которая была отправлена на указанный при регистрации электронный адрес";
    public static final String DELETE_USER_SUCCESS = "Учетная запись пользователя успешно удалена";
    public static final String USER_ALREADY_EXISTS = "Пользователь с таким email уже существует";
    public static final String USER_UPDATE_SUCCESS = "Пользователь успешно обновлен";
    public static final String USER_FOUND = "Пользователь найден";
    public static final String USERS_FOUND = "Пользователи найдены";
    public static final String USER_NOT_FOUND = "Извините, пользователь не найден";
    public static final String USER_NOT_FOUND_WITH_EMAIL = "Извините, пользователь с электронной почтой: '%s' не найден";
    public static final String LOCKED_ACCOUNT_SUCCESS = "Учетная запись успешно заблокирована";
    public static final String UNLOCKED_ACCOUNT_SUCCESS = "Учетная запись успешно разблокирована";
    public static final String SPECIALIZATION_NOT_FOUND = "В системе не найден ветеринар со специализацией '%s'";
    public static final String AVAILABLE_TIME_FOR_APPOINTMENT_FOUND = "Доступное время для приема успешно найдено";
    /*======================== End User API =====================================*/

    /*======================== Start Password API =====================================*/
    public static final String PASSWORD_CHANGE_SUCCESS = "Пароль успешно изменен, теперь можно закрыть форму.";
    public static final String PASSWORD_RESET_EMAIL_SENT = "Ссылка отправлена на вашу электронную почту, пожалуйста, проверьте ее для завершения запроса на смену пароля.";
    public static final String MISSING_PASSWORD = "Отсутствует токен или пароль";
    public static final String INVALID_RESET_TOKEN = "Недействительный токен сброса пароля";
    public static final String INVALID_EMAIL = "Пожалуйста, введите email, связанный с вашей учетной записью.";
    public static final String PASSWORD_RESET_SUCCESS = "Ваш пароль успешно сброшен!";
    public static final String PASSWORD_RESET_EMAIL_FAILED = "Не удалось отправить электронное письмо для сброса пароля";
    /*======================== End Password API =====================================*/

    /*======================== Start Appointment API =====================================*/
    public static final String APPOINTMENT_UPDATE_SUCCESS = "Запись успешно обновлена";
    public static final String APPOINTMENT_APPROVED_SUCCESS = "Запись успешно подтверждена";
    public static final String APPOINTMENT_DECLINED_SUCCESS = "Запись отклонена";
    public static final String APPOINTMENT_CANCELLED_SUCCESS = "Запись отменена";
    public static final String APPOINTMENT_DELETE_SUCCESS = "Запись успешно удалена";
    public static final String APPOINTMENT_BOOKED_SUCCESS = "Запись успешно зарегистрирована";
    public static final String APPOINTMENT_FOUND = "Запись найдена";
    public static final String APPOINTMENTS_FOUND = "Записи найдены";
    public static final String APPOINTMENT_NOT_FOUND = "Запись не найдена";
    public static final String APPOINTMENT_UPDATE_NOT_ALLOWED = "Невозможно обновить или отменить запись";
    /*======================== End Appointment API =====================================*/

    /*======================== Start Pet API =====================================*/
    public static final String PET_ADDED_SUCCESS = "Питомец успешно добавлен к записи";
    public static final String PET_UPDATE_SUCCESS = "Информация о питомце успешно обновлена";
    public static final String PET_DELETE_SUCCESS = "Питомец успешно удален";
    public static final String PET_FOUND = "Питомец(ы) найден(ы)";
    public static final String PET_NOT_FOUND = "Питомец не найден";
    public static final String NOT_ALLOWED_TO_DELETE_LAST_PET = "Единственный питомец не может быть удален, если необходимо отмените прием";
    /*======================== End Pet API =====================================*/

    /*======================== Start Review API =====================================*/
    public static final String REVIEW_NOT_ALLOWED = "Извините, оставить отзыв могут только пациенты, у которых была завершенная запись с этим ветеринаром";
    public static final String ALREADY_REVIEWED = "Вы уже оставили отзыв этому ветеринару, вы можете редактировать предыдущий отзыв";
    public static final String CANNOT_REVIEW = "Ветеринары не могут оставлять отзывы о себе";
    public static final String VET_OR_PATIENT_NOT_FOUND = "Ветеринар или пациент не найдены";
    public static final String NO_VETS_AVAILABLE = "По выбранной специальности на указанную дату и время нет доступных ветеринаров";
    public static final String REVIEW_SUBMIT_SUCCESS = "Отзыв успешно отправлен";
    public static final String REVIEW_UPDATE_SUCCESS = "Отзыв успешно обновлен";
    public static final String REVIEW_DELETE_SUCCESS = "Отзыв успешно удален";
    public static final String REVIEW_FOUND = "Отзыв(ы) найден(ы)";
    public static final String REVIEW_NOT_FOUND = "Отзыв(ы) не найден(ы)";
    /*======================== End Review API =====================================*/

    /*======================== Start authentication feedback =====================================*/
    public static final String EMPTY_PASSWORD = "Все поля обязательны для заполнения";
    public static final String CURRENT_PASSWORD_WRONG = "Текущий пароль указан неверно";
    public static final String NEW_PASSWORD_MUST_DIFFER = "Новый пароль должен отличаться от текущего";
    public static final String PASSWORD_MISMATCH = "Подтверждение пароля не совпадает";
    public static final String AUTHENTICATION_SUCCESS = "Аутентификация успешна";
    public static final String ACCOUNT_DISABLED = "Извините, ваша учетная запись отключена, вам необходимо завершить регистрацию: перейдите по ссылке, которая была отправлена на ваш электронный адрес или обратитесь в службу поддержки";
    public static final Object INVALID_PASSWORD = "Неверное имя пользователя или пароль";
    /*======================== End authentication feedback =====================================*/

    /*======================== Start Token API =====================================*/
    public static final String INVALID_TOKEN = "INVALID";
    public static final String TOKEN_ALREADY_VERIFIED = "VERIFIED";
    public static final String EXPIRED_TOKEN = "EXPIRED";
    public static final String VALID_TOKEN = "VALID";
    public static final String TOKEN_VALIDATION_ERROR = "Ошибка проверки токена";
    public static final String TOKEN_SAVED_SUCCESS = "Токен подтверждения успешно сохранен";
    public static final String TOKEN_DELETE_SUCCESS = "Токен пользователя успешно удален";
    public static final String INVALID_VERIFICATION_TOKEN = "Недействительный токен подтверждения";
    public static final String NEW_VERIFICATION_TOKEN_SENT = "Новая ссылка для подтверждения отправлена на вашу почту. Пожалуйста, перейдите по ней для завершения регистрации.";
    public static final String INVALID_OR_EXPIRED_JWT = "Недействительный или просроченный токен JWT";
    /*======================== End Token API =====================================*/

    /*======================== Start Role API =====================================*/
    public static final String ROLE_NOT_FOUND = "Роль не найдена";
    /*======================== End Role API =====================================*/

    /*======================== Start Photo API =====================================*/
    public static final String PHOTO_UPDATE_SUCCESS = "Фотография успешно обновлена";
    public static final String PHOTO_REMOVE_SUCCESS = "Фотография успешно удалена";
    /*======================== End Photo API =====================================*/

    /*======================== Start General feedback =====================================*/
    public static final String SUCCESS = "Успешно";
    public static final String RESOURCE_FOUND = "Ресурс найден";
    public static final String SENDER_RECIPIENT_NOT_FOUND = "Отправитель или получатель не найдены";
    public static final String ERROR = "Произошла ошибка";
    public static final String RESOURCE_NOT_FOUND = "Ресурс не найден";
    public static final String OPERATION_NOT_ALLOWED = "Операция не разрешена";
    /*======================== End general feedback =====================================*/

    /*======================== Start VetBiography API =====================================*/
    public static final String VETERINARIAN_INFO_NOT_AVAILABLE = "Информация о ветеринаре пока отсутствует, но вскоре появится!";
    public static final String VETERINARIAN_NOT_FOUND = "Ветеринар не найден";
    public static final String BIOGRAPHY_FOUND = "Биография найдена";
    public static final String BIOGRAPHY_NOT_FOUND = "Биография не найдена";
    public static final String BIOGRAPHY_SAVED_SUCCESS = "Биография успешно сохранена";
    public static final String BIOGRAPHY_UPDATED_SUCCESS = "Биография успешно обновлена";
    public static final String BIOGRAPHY_DELETED_SUCCESS = "Биография успешно удалена";
    /*======================== End VetBiography API =====================================*/

    /*======================== Start Validator API =====================================*/
    public static final String INVALID_PASSWORD_FORMAT = "Пароль должен быть не менее 8 символов и содержать буквы и цифры латинского алфавита!";
    public static final String INVALID_EMAIL_FORMAT = "Упс! Кажется, в адресе электронной почты ошибка. Проверьте, что ваш email введён правильно.";
    public static final String INVALID_PHONE_FORMAT = "Упс! Кажется, в номере телефона ошибка. Проверьте, что номер телефона введён правильно.";
    /*======================== End Validator API =====================================*/
}
