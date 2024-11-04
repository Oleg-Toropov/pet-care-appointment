package com.olegtoropoff.petcareappointment.utils;

public class FeedBackMessage {
    public static final String CREATE_SUCCESS = "Ресурс успешно создан";
    public static final String UPDATE_SUCCESS = "Ресурс успешно обновлен";
    public static final String RESOURCE_FOUND = "Ресурс найден";
    public static final String DELETE_SUCCESS = "Ресурс успешно удален";
    public static final String USER_ALREADY_EXISTS = "Пользователь с таким email уже существует";
    public static final String ALREADY_APPROVED = "Извините, эта встреча уже подтверждена";
    public static final String RESOURCE_NOT_FOUND = "Ресурс не найден";
    public static final String SENDER_RECIPIENT_NOT_FOUND = "Отправитель или получатель не найден";
    public static final String NOT_ALLOWED = "Извините, только пациенты, завершившие прием у этого ветеринара, могут оставить отзыв";
    public static final String ALREADY_REVIEWED = "Вы уже оценили этого ветеринара, вы можете изменить свой предыдущий отзыв";
    public static final String CANNOT_REVIEW = "Ветеринары не могут оставлять отзывы о себе";
    public static final String VET_OR_PATIENT_NOT_FOUND = "Ветеринар или пациент не найден";
    public static final String SERVER_ERROR = "Внутренняя ошибка сервера";
    public static final String NO_VETS_AVAILABLE = "Нет доступных ветеринаров на запрашиваемые дату и время";
    public static final String SPECIALIZATION_NOT_FOUND = "В системе не найден ветеринар со специализацией '%s'";
    public static final String  USER_NOT_FOUND = "Пользователь не найден";
    public static final String  CURRENT_PASSWORD_WRONG = "Текущий пароль указан неверно";
    public static final String  ALL_FIELDS_REQUIRED = "Все поля должны быть заполнены";
    public static final String  NEW_PASSWORD_MUST_DIFFER = "Новый пароль должен отличаться от текущего";
    public static final String  PASSWORDS_MUST_MATCH = "Пароль и подтверждение пароля должны совпадать";
    public static final String  NOT_ALLOWED_TO_DELETE_LAST_PET = "Единственный питомец не может быть удален, если необходимо отмените прием";
    public static final String APPOINTMENT_CANNOT_BE_CANCELLED = "Данный прием не может быть отменен";
    public static final String APPOINTMENT_ALREADY_APPROVED = "Прием уже подтвержден";
    public static final String APPOINTMENT_SUMMARY_SUCCESS_MESSAGE = "Сводка по приемам успешно получена";
    public static final String APPOINTMENT_SUMMARY_ERROR_MESSAGE = "Ошибка при получении сводки по приемам: ";
    public static final String USER_ACCOUNT_LOCKED_SUCCESSFULLY = "Учетная запись пользователя успешно заблокирована";
    public static final String USER_ACCOUNT_UNLOCKED_SUCCESSFULLY = "Учетная запись пользователя успешно разблокирована";
}
