package com.olegtoropoff.petcareappointment.utils;

/**
 * Defines URL mappings for the application's API endpoints.
 * <p>
 * This class organizes endpoint paths for various resources,
 * such as users, appointments, pets, photos, reviews, veterinarians,
 * patients, authentication, roles, and biographies. The mappings are
 * structured for clarity and easy maintenance.
 */
public class UrlMapping {

    /**
     * Base path for API version 1.
     */
    public static final String API = "/api/v1";

    /*========================== Start User API =================================*/
    /**
     * Base path for user-related endpoints.
     */
    public static final String USERS = API + "/users";

    /**
     * Endpoint for registering a new user.
     */
    public static final String REGISTER_USER = "/register";

    /**
     * Endpoint for updating an existing user by their ID.
     */
    public static final String UPDATE_USER = "/user/{userId}/update";

    /**
     * Endpoint for retrieving a user by their ID.
     */
    public static final String GET_USER_BY_ID = "/user/{userId}";

    /**
     * Endpoint for deleting a user by their ID.
     */
    public static final String DELETE_USER_BY_ID = "/user/{userId}/delete";

    /**
     * Endpoint for counting all registered users.
     */
    public static final String COUNT_ALL_USERS = "/count/users";

    /**
     * Endpoint for counting all veterinarians.
     */
    public static final String COUNT_ALL_VETERINARIANS = "/count/veterinarians";

    /**
     * Endpoint for counting all patients.
     */
    public static final String COUNT_ALL_PATIENTS = "/count/patients";

    /**
     * Endpoint for aggregating user data.
     */
    public static final String AGGREGATE_USERS = "/aggregated-users";

    /**
     * Endpoint for aggregating user data by account status.
     */
    public static final String AGGREGATE_USERS_BY_STATUS = "/account/aggregated-by-status";

    /**
     * Endpoint for locking a user's account by their ID.
     */
    public static final String LOCK_USER_ACCOUNT = "/account/{userId}/lock-user-account";

    /**
     * Endpoint for unlocking a user's account by their ID.
     */
    public static final String UNLOCK_USER_ACCOUNT = "/account/{userId}/unLock-user-account";

    /**
     * Endpoint for retrieving a user's photo by their ID.
     */
    public static final String GET_PHOTO_BY_USER_ID = "/user/{userId}/photo";

    /**
     * Endpoint for changing a user's password by their ID.
     */
    public static final String CHANGE_PASSWORD = "/user/{userId}/change-password";
    /*========================= End User API ================================*/

    /*========================== Start Appointment API =================================*/
    /**
     * Base path for appointment-related endpoints.
     */
    public static final String APPOINTMENTS = API + "/appointments";

    /**
     * Endpoint for booking a new appointment.
     */
    public static final String BOOK_APPOINTMENT = "/book-appointment";

    /**
     * Endpoint for updating an appointment by its ID.
     */
    public static final String UPDATE_APPOINTMENT = "/appointment/{id}/update";

    /**
     * Endpoint for adding a pet to an existing appointment by its ID.
     */
    public static final String ADD_PET_APPOINTMENT = "/appointment/{id}/add-pet";

    /**
     * Endpoint for retrieving all appointments.
     */
    public static final String ALL_APPOINTMENT = "/all";

    /**
     * Endpoint for fetching an appointment by its ID.
     */
    public static final String GET_APPOINTMENT_BY_ID = "/appointment/{id}/fetch/appointment";

    /**
     * Endpoint for deleting an appointment by its ID.
     */
    public static final String DELETE_APPOINTMENT = "/appointment/{id}/delete";

    /**
     * Endpoint for canceling an appointment by its ID.
     */
    public static final String CANCEL_APPOINTMENT = "/appointment/{id}/cancel";

    /**
     * Endpoint for approving an appointment by its ID.
     */
    public static final String APPROVE_APPOINTMENT = "/appointment/{id}/approve";

    /**
     * Endpoint for declining an appointment by its ID.
     */
    public static final String DECLINE_APPOINTMENT = "/appointment/{id}/decline";

    /**
     * Endpoint for counting all appointments.
     */
    public static final String COUNT_APPOINTMENT = "/count/appointments";

    /**
     * Endpoint for retrieving a summary of all appointments.
     */
    public static final String APPOINTMENT_SUMMARY = "/summary/appointments-summary";
    /*========================= End Appointment API ================================*/

    /*============================ Start Pet API ===================================*/
    /**
     * Base path for pet-related endpoints.
     */
    public static final String PETS = API + "/pets/";

    /**
     * Endpoint for deleting a pet by its ID.
     */
    public static final String DELETE_PET_BY_ID = "/pet/{petId}/delete";

    /**
     * Endpoint for updating the details of a pet by its ID.
     */
    public static final String UPDATE_PET = "/pet/{petId}/update";

    /**
     * Endpoint for retrieving the available pet types.
     */
    public static final String GET_PET_TYPES = "/get-types";

    /**
     * Endpoint for retrieving the available pet colors.
     */
    public static final String GET_PET_COLORS = "/get-pet-colors";

    /**
     * Endpoint for retrieving the available pet breeds.
     */
    public static final String GET_PET_BREEDS = "/get-pet-breeds";
    /*============================ End Pet API ===================================*/

    /*============================ Start Photo API ===================================*/
    /**
     * Base path for photo-related endpoints.
     */
    public static final String PHOTOS = API + "/photos";

    /**
     * Endpoint for uploading a photo.
     */
    public static final String UPLOAD_PHOTO = "/photo/upload";

    /**
     * Endpoint for deleting a photo by its ID and associated user ID.
     */
    public static final String DELETE_PHOTO = "/photo/{photoId}/{userId}/delete";

    /**
     * Endpoint for updating a photo by its ID.
     */
    public static final String UPDATE_PHOTO = "/photo/{photoId}/update";
    /*============================ End Photo API ===================================*/

    /*============================ Start Review API ===================================*/
    /**
     * Base path for review-related endpoints.
     */
    public static final String REVIEWS = API + "/reviews";

    /**
     * Endpoint for submitting a new review.
     */
    public static final String SUBMIT_REVIEW = "/submit-review";

    /**
     * Endpoint for deleting a review by its ID.
     */
    public static final String DELETE_REVIEW = "/review/{reviewId}/delete";
    /*============================ End Review API ===================================*/

    /*============================ Start Veterinarian API ===================================*/
    /**
     * Base path for veterinarian-related endpoints.
     */
    public static final String VETERINARIANS = API + "/veterinarians";

    /**
     * Endpoint for retrieving all veterinarians with details.
     */
    public static final String GET_ALL_VETERINARIANS = "/get-all-veterinarians";

    /**
     * Endpoint for retrieving all veterinarians.
     */
    public static final String GET_VETERINARIANS = "/get-veterinarians";

    /**
     * Endpoint for searching a veterinarian for an appointment.
     */
    public static final String SEARCH_VETERINARIAN_FOR_APPOINTMENT = "/search-veterinarian";

    /**
     * Endpoint for retrieving all specializations available for veterinarians.
     */
    public static final String GET_ALL_SPECIALIZATIONS = "/vet/get-all-specialization";

    /**
     * Endpoint for aggregating veterinarians by specialization.
     */
    public static final String AGGREGATE_VETERINARIANS_BY_SPECIALIZATION = "/vet/get-by-specialization";

    /**
     * Endpoint for retrieving available times for a specific veterinarian to book an appointment.
     */
    public static final String GET_AVAILABLE_TIME_FOR_BOOK_APPOINTMENT = "/{vetId}/available-times";
    /*============================ End Veterinarian API ===================================*/

    /*============================ Start Patient ===================================*/
    /**
     * Base path for patient-related endpoints.
     */
    public static final String PATIENTS = API + "/patients";

    /**
     * Endpoint for retrieving all patients.
     */
    public static final String GET_ALL_PATIENTS = "/get-all-patients";
    /*============================ End Patient ===================================*/

    /*============================ Start Auth ===================================*/
    /**
     * Base path for authentication-related endpoints.
     */
    public static final String AUTH = API + "/auth";

    /**
     * Endpoint for user login.
     */
    public static final String LOGIN = "/login";

    /**
     * Endpoint for requesting a password reset.
     */
    public static final String REQUEST_PASSWORD_RESET = "/request-password-reset";

    /**
     * Endpoint for resetting the user's password.
     */
    public static final String RESET_PASSWORD = "/reset-password";

    /**
     * Endpoint for verifying a user's email.
     */
    public static final String VERIFY_EMAIL = "/verify-your-email";

    /**
     * Endpoint for resending the email verification token.
     */
    public static final String RESEND_VERIFICATION_TOKEN = "/resend-verification-token";
    /*============================ End Auth ===================================*/

    /*============================ Start Verification token ===================================*/
    /**
     * Base path for token verification-related endpoints.
     */
    public static final String TOKEN_VERIFICATION = API + "/verification";

    /**
     * Endpoint for checking token expiration.
     */
    public static final String CHECK_TOKEN_EXPIRATION = "/check-token-expiration";
    /*============================ End Verification token ===================================*/

    /*============================ Start VetBiography ===================================*/
    /**
     * Base path for veterinarian biography-related endpoints.
     */
    public static final String BIOGRAPHIES = API + "/biographies";

    /**
     * Endpoint for retrieving a biography by veterinarian ID.
     */
    public static final String GET_BIOGRAPHY_BY_VET_ID = "/biography/{vetId}";

    /**
     * Endpoint for saving a biography.
     */
    public static final String SAVE_BIOGRAPHY = "/biography/{vetId}/save";

    /**
     * Endpoint for updating a biography.
     */
    public static final String UPDATE_BIOGRAPHY = "/biography/{id}/update";
    /*============================ End VetBiography ===================================*/
}
