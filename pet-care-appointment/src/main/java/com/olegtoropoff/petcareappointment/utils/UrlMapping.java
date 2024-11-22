package com.olegtoropoff.petcareappointment.utils;

public class UrlMapping {

    public static final String API = "/api/v1";

    /*========================== Start User API =================================*/
    public static final String USERS = API + "/users";
    public static final String REGISTER_USER = "/register";
    public static final String UPDATE_USER = "/user/{userId}/update";
    public static final String GET_ALL_USERS = "/all-users";
    public static final String GET_USER_BY_ID = "/user/{userId}";
    public static final String DELETE_USER_BY_ID = "/user/{userId}/delete";
    public static final String COUNT_ALL_USERS = "/count/users";
    public static final String COUNT_ALL_VETERINARIANS = "/count/veterinarians";
    public static final String COUNT_ALL_PATIENTS = "/count/patients";
    public static final String AGGREGATE_USERS = "/aggregated-users";
    public static final String AGGREGATE_USERS_BY_STATUS = "/account/aggregated-by-status";
    public static final String LOCK_USER_ACCOUNT = "/account/{userId}/lock-user-account";
    public static final String UNLOCK_USER_ACCOUNT = "/account/{userId}/unLock-user-account";
    /*========================= End User API ================================*/

    /*========================== Start Appointment API =================================*/
    public static final String APPOINTMENTS = API + "/appointments";
    public static final String BOOK_APPOINTMENT = "/book-appointment";
    public static final String UPDATE_APPOINTMENT = "/appointment/{id}/update";
    public static final String ADD_PET_APPOINTMENT = "/appointment/{id}/add-pet";
    public static final String ALL_APPOINTMENT = "/all";
    public static final String GET_APPOINTMENT_BY_ID = "/appointment/{id}/fetch/appointment";
    public static final String GET_APPOINTMENT_BY_NO = "/appointment/{appointmentNo}/appointment";
    public static final String DELETE_APPOINTMENT = "/appointment/{id}/delete";
    public static final String CANCEL_APPOINTMENT = "appointment/{id}/cancel";
    public static final String APPROVE_APPOINTMENT = "/appointment/{id}/approve";
    public static final String DECLINE_APPOINTMENT = "/appointment/{id}/decline";
    public static final String COUNT_APPOINTMENT = "/count/appointments";
    public static final String APPOINTMENT_SUMMARY = "/summary/appointments-summary";
    /*========================= End Appointment API ================================*/

    /*============================ Start Pet API ===================================*/
    public static final String PETS = API + "/pets/";
    public static final String SAVE_PETS_FOR_APPOINTMENT = "/save-pets/";
    public static final String GET_PET_BY_ID = "/pet/{petId}/pet";
    public static final String DELETE_PET_BY_ID = "/pet/{petId}/delete";
    public static final String UPDATE_PET = "/pet/{petId}/update";
    public static final String GET_PET_TYPES = "/get-types";
    public static final String GET_PET_COLORS = "/get-pet-colors";
    public static final String GET_PET_BREEDS = "/get-pet-breeds";
    /*============================ End Pet API ===================================*/

    /*============================ Start Photo API ===================================*/
    public static final String PHOTOS = API + "/photos";
    public static final String UPLOAD_PHOTO = "/photo/upload";
    public static final String GET_PHOTO_BY_ID = "/photo/{photoId}/photo";
    public static final String DELETE_PHOTO = "/photo/{photoId}/{userId}/delete";
    public static final String UPDATE_PHOTO = "/photo/{photoId}/update";
    /*============================ End Photo API ===================================*/

    /*============================ Start Review API ===================================*/
    public static final String REVIEWS = API + "/reviews";
    public static final String SUBMIT_REVIEW = "/submit-review";
    public static final String GET_USER_REVIEWS = "/user/{userId}/reviews";
    public static final String UPDATE_REVIEW = "/review/{reviewId}/update";
    public static final String DELETE_REVIEW = "/review/{reviewId}/delete";
    public static final String GET_AVERAGE_RATING = "/vet/{vetId}/get-average-rating";
    /*============================ End Review API ===================================*/

    /*============================ Start Veterinarian API ===================================*/
    public static final String VETERINARIANS = API + "/veterinarians";
    public static final String GET_ALL_VETERINARIANS = "/get-all-veterinarians";
    public static final String SEARCH_VETERINARIAN_FOR_APPOINTMENT = "/search-veterinarian";
    public static final String GET_ALL_SPECIALIZATIONS = "/vet/get-all-specialization";
    public static final String AGGREGATE_VETERINARIANS_BY_SPECIALIZATION = "/vet/get-by-specialization";
    public static final String GET_AVAILABLE_TIME_FOR_BOOK_APPOINTMENT = "/{vetId}/available-times";
    /*============================ End Veterinarian API ===================================*/

    /*============================ Start Change Password API ===================================*/
    public static final String CHANGE_PASSWORD = "/user/{userId}/change-password";
    /*============================ End Change Password API ===================================*/

    /*============================ Start Patient ===================================*/
    public static final String PATIENTS = API + "/patients";
    public static final String GET_ALL_PATIENTS = "/get-all-patients";
    /*============================ End Patient ===================================*/

    /*============================ Start Auth ===================================*/
    public static final String AUTH = API + "/auth";
    public static final String LOGIN = "/login";
    public static final String REQUEST_PASSWORD_RESET = "/request-password-reset" ;
    public static final String RESET_PASSWORD = "/reset-password" ;
    public static final String VERIFY_EMAIL = "/verify-your-email";
    public static final String RESEND_VERIFICATION_TOKEN = "/resend-verification-token";
    /*============================ End Auth ===================================*/

    /*============================ Start Verification token ===================================*/
    public static final String TOKEN_VERIFICATION = API + "/verification";
    public static final String VALIDATE_TOKEN = "/validate-token";
    public static final String CHECK_TOKEN_EXPIRATION = "/check-token-expiration";
    public static final String SAVE_TOKEN = "/user/save-token" ;
    public static final String GENERATE_NEW_TOKEN_FOR_USER = "/generate-new-token";
    public static final String DELETE_TOKEN = "/delete-token";
    /*============================ End Verification token ===================================*/

    /*============================ Start Role ===================================*/
    public static final String ROLES = API + "/roles";
    public static final String GET_ALL_ROLES = "/all-roles";
    public static final String GET_ROLE_BY_ID = "/role/get-by-id/role";
    public static final String GET_ROLE_BY_NAME = "/role/get-by-name";
    /*============================ End Role ===================================*/
}
