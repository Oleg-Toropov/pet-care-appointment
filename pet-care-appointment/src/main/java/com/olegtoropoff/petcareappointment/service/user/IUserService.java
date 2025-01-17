package com.olegtoropoff.petcareappointment.service.user;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.request.UserUpdateRequest;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Interface defining user-related operations in the application.
 * Provides methods for managing users, including registration, updates, deletion,
 * and retrieval of user information.
 */
public interface IUserService {

    /**
     * Registers a new user in the system.
     *
     * @param request the registration details of the user.
     * @return the newly registered {@link User}.
     */
    User register(RegistrationRequest request);

    /**
     * Updates an existing user's information.
     *
     * @param userId  the ID of the user to update.
     * @param request the updated user details.
     * @return the updated {@link User}.
     */
    User update(Long userId, UserUpdateRequest request);

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of {@link UserDto} objects representing all users.
     */
    List<UserDto> getAllUsers();

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve.
     * @return the {@link User} entity with the specified ID.
     * @throws com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException
     *         if no user is found with the given ID.
     */
    User findById(Long userId);

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete.
     * @throws com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException
     *         if no user is found with the given ID.
     */
    void deleteById(Long userId);

    /**
     * Retrieves detailed information about a user, including appointments and reviews.
     *
     * @param userId the ID of the user to retrieve.
     * @return a {@link UserDto} containing detailed user information.
     * @throws SQLException if an error occurs while retrieving user-related data.
     */
    UserDto getUserWithDetails(Long userId) throws SQLException;

    /**
     * Counts the total number of veterinarians in the system.
     *
     * @return the count of users with the role "VET".
     */
    long countVeterinarians();

    /**
     * Counts the total number of patients in the system.
     *
     * @return the count of users with the role "PATIENT".
     */
    long countPatients();

    /**
     * Counts the total number of users in the system.
     *
     * @return the total count of users.
     */
    long countAllUsers();

    /**
     * Aggregates user counts by their registration month and type.
     *
     * @return a map where the key is the registration month (e.g., "January"),
     *         and the value is another map containing user types as keys and their counts as values.
     */
    Map<String, Map<String, Long>> aggregateUsersByMonthAndType();

    /**
     * Aggregates user counts by their enabled status and type.
     *
     * @return a map where the key is the enabled status ("Enabled" or "Non-Enabled"),
     *         and the value is another map containing user types as keys and their counts as values.
     */
    Map<String, Map<String, Long>> aggregateUsersByEnabledStatusAndType();

    /**
     * Locks a user's account, preventing them from logging in.
     *
     * @param userId the ID of the user whose account will be locked.
     */
    void lockUserAccount(Long userId);

    /**
     * Unlocks a user's account, allowing them to log in again.
     *
     * @param userId the ID of the user whose account will be unlocked.
     */
    void unLockUserAccount(Long userId);

    /**
     * Retrieves the URL of the photo associated with a user.
     *
     * @param userId the ID of the user whose photo URL is to be retrieved.
     * @return a {@link String} representing the URL of the user's photo,
     *         or {@code null} if the user has no associated photo.
     * @throws ResourceNotFoundException if the user with the specified ID is not found.
     */
    String getPhotoUrlByUserId(Long userId);
}
