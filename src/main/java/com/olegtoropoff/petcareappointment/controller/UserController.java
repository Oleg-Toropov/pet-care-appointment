package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.exception.UserAlreadyExistsException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.request.ChangePasswordRequest;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.request.UserUpdateRequest;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.password.IChangePasswordService;
import com.olegtoropoff.petcareappointment.service.user.IUserService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * Controller for managing users in the system. Provides endpoints for user registration,
 * updates, password changes, and data retrieval.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
public class UserController {
    private final IUserService userService;
    private final EntityConverter<User, UserDto> entityConverter;
    private final IChangePasswordService changePasswordService;
    private final RabbitMQProducer rabbitMQProducer;

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user details
     * @return a {@link ResponseEntity} containing the created user's details
     */
    @PostMapping(UrlMapping.REGISTER_USER)
    public ResponseEntity<CustomApiResponse> register(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.register(request);
            rabbitMQProducer.sendMessage("RegistrationCompleteEvent:" + user.getId());
            UserDto registeredUserDto = entityConverter.mapEntityToDto(user, UserDto.class);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.CREATE_USER_SUCCESS, registeredUserDto));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new CustomApiResponse(e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Updates user details.
     *
     * @param userId the ID of the user to update
     * @param request the update request containing new user details
     * @return a {@link ResponseEntity} containing the updated user's details
     */
    @PutMapping(UrlMapping.UPDATE_USER)
    public ResponseEntity<CustomApiResponse> update(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        try {
            User user = userService.update(userId, request);
            UserDto updatedUserDto = entityConverter.mapEntityToDto(user, UserDto.class);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.USER_UPDATE_SUCCESS, updatedUserDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new CustomApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a {@link ResponseEntity} containing a list of all users
     */
    @GetMapping(UrlMapping.GET_ALL_USERS)
    public ResponseEntity<CustomApiResponse> getAllUsers() {
        try {
            List<UserDto> usersDto = userService.getAllUsers();
            return ResponseEntity.status(FOUND).body(new CustomApiResponse(FeedBackMessage.USERS_FOUND, usersDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Retrieves user details by ID.
     *
     * @param userId the ID of the user
     * @return a {@link ResponseEntity} containing the user's details
     */
    @GetMapping(UrlMapping.GET_USER_BY_ID)
    public ResponseEntity<CustomApiResponse> getById(@PathVariable Long userId) {
        try {
            UserDto userDto = userService.getUserWithDetails(userId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.USER_FOUND, userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId the ID of the user to delete
     * @return a {@link ResponseEntity} indicating the success of the operation
     */
    @DeleteMapping(UrlMapping.DELETE_USER_BY_ID)
    public ResponseEntity<CustomApiResponse> deleteById(@PathVariable Long userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.DELETE_USER_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Changes a user's password.
     *
     * @param userId the ID of the user
     * @param request the password change request containing current and new passwords
     * @return a {@link ResponseEntity} indicating the success of the operation
     */
    @PutMapping(UrlMapping.CHANGE_PASSWORD)
    public ResponseEntity<CustomApiResponse> changePassword(@PathVariable Long userId,
                                                            @RequestBody ChangePasswordRequest request) {
        try {
            changePasswordService.changePassword(userId, request);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.PASSWORD_CHANGE_SUCCESS, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new CustomApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Retrieves the number of veterinarians in the system.
     *
     * @return the count of veterinarians
     */
    @GetMapping(UrlMapping.COUNT_ALL_VETERINARIANS)
    public long countVeterinarians() {
        return userService.countVeterinarians();
    }

    /**
     * Retrieves the number of patients in the system.
     *
     * @return the count of patients
     */
    @GetMapping(UrlMapping.COUNT_ALL_PATIENTS)
    public long countPatients() {
        return userService.countPatients();
    }

    /**
     * Retrieves the total number of users in the system.
     *
     * @return the count of users
     */
    @GetMapping(UrlMapping.COUNT_ALL_USERS)
    public long countUsers() {
        return userService.countAllUsers();
    }

    /**
     * Aggregates users by month and type.
     *
     * @return a {@link ResponseEntity} containing aggregated user data
     */
    @GetMapping(UrlMapping.AGGREGATE_USERS)
    public ResponseEntity<CustomApiResponse> aggregateUserByMonthAndType() {
        try {
            Map<String, Map<String, Long>> aggregateUsers = userService.aggregateUsersByMonthAndType();
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, aggregateUsers));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Aggregates users by their enabled status.
     *
     * @return a {@link ResponseEntity} containing aggregated user data
     */
    @GetMapping(UrlMapping.AGGREGATE_USERS_BY_STATUS)
    public ResponseEntity<CustomApiResponse> getAggregateUsersByEnabledStatus() {
        try {
            Map<String, Map<String, Long>> aggregateData = userService.aggregateUsersByEnabledStatusAndType();
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, aggregateData));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Locks a user account.
     *
     * @param userId the ID of the user to lock
     * @return a {@link ResponseEntity} indicating the success of the operation
     */
    @PutMapping(UrlMapping.LOCK_USER_ACCOUNT)
    public ResponseEntity<CustomApiResponse> lockUserAccount(@PathVariable Long userId) {
        try {
            userService.lockUserAccount(userId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.LOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Unlocks a user account.
     *
     * @param userId the ID of the user to unlock
     * @return a {@link ResponseEntity} indicating the success of the operation
     */
    @PutMapping(UrlMapping.UNLOCK_USER_ACCOUNT)
    public ResponseEntity<CustomApiResponse> unLockUserAccount(@PathVariable Long userId) {
        try {
            userService.unLockUserAccount(userId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    /**
     * Retrieves the URL of a user's photo by their ID.
     *
     * @param userId the ID of the user
     * @return a {@link ResponseEntity} containing the URL of the user's photo
     */
    @GetMapping(value = UrlMapping.GET_PHOTO_BY_USER_ID)
    public ResponseEntity<CustomApiResponse> getPhotoUrlByUserId(@PathVariable Long userId) {
        try {
            String photoUrl = userService.getPhotoUrlByUserId(userId);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.RESOURCE_FOUND, photoUrl));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new CustomApiResponse(FeedBackMessage.RESOURCE_NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        }
    }
}
