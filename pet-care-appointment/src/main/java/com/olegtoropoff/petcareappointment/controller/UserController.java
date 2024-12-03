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
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.service.password.IChangePasswordService;
import com.olegtoropoff.petcareappointment.service.user.IUserService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
public class UserController {
    private final IUserService userService;
    private final EntityConverter<User, UserDto> entityConverter;
    private final IChangePasswordService changePasswordService;
    private final RabbitMQProducer rabbitMQProducer;

    @PostMapping(UrlMapping.REGISTER_USER)
    public ResponseEntity<ApiResponse> register(@RequestBody RegistrationRequest request) {
        try {
            User user = userService.register(request);
            rabbitMQProducer.sendMessage("RegistrationCompleteEvent:" + user.getId());
            UserDto registeredUserDto = entityConverter.mapEntityToDto(user, UserDto.class);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.CREATE_USER_SUCCESS, registeredUserDto));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_USER)
    public ResponseEntity<ApiResponse> update(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        try {
            User user = userService.update(userId, request);
            UserDto updatedUserDto = entityConverter.mapEntityToDto(user, UserDto.class);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.USER_UPDATE_SUCCESS, updatedUserDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.GET_ALL_USERS)
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<UserDto> usersDto = userService.getAllUsers();
            return ResponseEntity.status(FOUND).body(new ApiResponse(FeedBackMessage.USERS_FOUND, usersDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.GET_USER_BY_ID)
    public ResponseEntity<ApiResponse> getById(@PathVariable Long userId) {
        try {
            UserDto userDto = userService.getUserWithDetails(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.USER_FOUND, userDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_USER_BY_ID)
    public ResponseEntity<ApiResponse> deleteById(@PathVariable Long userId) {
        try {
            userService.deleteById(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.DELETE_USER_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.CHANGE_PASSWORD)
    public ResponseEntity<ApiResponse> changePassword(@PathVariable Long userId,
                                                      @RequestBody ChangePasswordRequest request) {
        try {
            changePasswordService.changePassword(userId, request);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.PASSWORD_CHANGE_SUCCESS, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.COUNT_ALL_VETERINARIANS)
    public long countVeterinarians() {
        return userService.countVeterinarians();
    }

    @GetMapping(UrlMapping.COUNT_ALL_PATIENTS)
    public long countPatients() {
        return userService.countPatients();
    }

    @GetMapping(UrlMapping.COUNT_ALL_USERS)
    public long countUsers() {
        return userService.countAllUsers();
    }

    @GetMapping(UrlMapping.AGGREGATE_USERS)
    public ResponseEntity<ApiResponse> aggregateUserByMonthAndType() {
        try {
            Map<String, Map<String, Long>> aggregateUsers = userService.aggregateUsersByMonthAndType();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, aggregateUsers));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(UrlMapping.AGGREGATE_USERS_BY_STATUS)
    public ResponseEntity<ApiResponse> getAggregateUsersByEnabledStatus() {
        try {
            Map<String, Map<String, Long>> aggregateData = userService.aggregateUsersByEnabledStatusAndType();
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.RESOURCE_FOUND, aggregateData));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.LOCK_USER_ACCOUNT)
    public ResponseEntity<ApiResponse> lockUserAccount(@PathVariable Long userId) {
        try {
            userService.lockUserAccount(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.LOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @PutMapping(UrlMapping.UNLOCK_USER_ACCOUNT)
    public ResponseEntity<ApiResponse> unLockUserAccount(@PathVariable Long userId) {
        try {
            userService.unLockUserAccount(userId);
            return ResponseEntity.ok(new ApiResponse(FeedBackMessage.UNLOCKED_ACCOUNT_SUCCESS, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(FeedBackMessage.ERROR, null));
        }
    }

    @GetMapping(value = UrlMapping.GET_PHOTO_BY_USER_ID, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPhotoByUserId(@PathVariable Long userId) {
        try {
            byte[] photoBytes = userService.getPhotoByUserId(userId);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(photoBytes);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }
}
