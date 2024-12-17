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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private IUserService userService;

    @Mock
    private EntityConverter<User, UserDto> entityConverter;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @Mock
    private IChangePasswordService changePasswordService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void getById_WhenUserExists_ReturnsUserWithStatusOk() throws SQLException {
        Long userId = 4L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setFirstName("Иван");
        userDto.setLastName("Иванов");
        when(userService.getUserWithDetails(userId)).thenReturn(userDto);

        ResponseEntity<ApiResponse> response = userController.getById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Пользователь найден", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(userDto, response.getBody().getData());
    }

    @Test
    public void getById_WhenUserNotFound_ReturnsStatusNotFound() throws SQLException {
        Long userId = 100L;
        String errorMessage = "Извините, пользователь не найден";
        when(userService.getUserWithDetails(userId)).thenThrow(new ResourceNotFoundException(errorMessage));

        ResponseEntity<ApiResponse> response = userController.getById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void getById_WhenInternalErrorOccurs_ReturnsStatusInternalServerError() throws SQLException {
        Long userId = 4L;
        when(userService.getUserWithDetails(userId)).thenThrow(new RuntimeException());

        ResponseEntity<ApiResponse> response = userController.getById(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Произошла ошибка", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void deleteById_WhenValidUserId_ReturnsSuccess() {
        Long userId = 5L;
        doNothing().when(userService).deleteById(userId);

        ResponseEntity<ApiResponse> response = userController.deleteById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Учетная запись пользователя успешно удалена", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void deleteById_WhenUserNotFound_ReturnsNotFound() {
        Long userId = 100L;
        String errorMessage = "Извините, пользователь не найден";
        doThrow(new ResourceNotFoundException(errorMessage)).when(userService).deleteById(userId);

        ResponseEntity<ApiResponse> response = userController.deleteById(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void deleteById_WhenInternalErrorOccurs_ReturnsInternalServerError() {
        Long userId = 5L;
        doThrow(new RuntimeException()).when(userService).deleteById(userId);

        ResponseEntity<ApiResponse> response = userController.deleteById(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Произошла ошибка", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void update_WhenValidUserId_ReturnsUpdatedUser() {
        Long userId = 4L;
        String firstName = "UpdatedName";
        String lastName = "UpdatedLastName";
        String phoneNumber = "89124000000";

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName(firstName);
        updateRequest.setLastName(lastName);
        updateRequest.setPhoneNumber(phoneNumber);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        updatedUser.setPhoneNumber(phoneNumber);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setFirstName(firstName);
        updatedUserDto.setLastName(lastName);
        updatedUserDto.setPhoneNumber(phoneNumber);

        when(userService.update(userId, updateRequest)).thenReturn(updatedUser);
        when(entityConverter.mapEntityToDto(updatedUser, UserDto.class)).thenReturn(updatedUserDto);

        ResponseEntity<ApiResponse> response = userController.update(userId, updateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.USER_UPDATE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(updatedUserDto, response.getBody().getData());
    }

    @Test
    public void update_WhenInvalidData_ReturnsBadRequest() {
        Long userId = 4L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("UpdatedName");
        updateRequest.setLastName("UpdatedLastName");
        updateRequest.setPhoneNumber("891000000");
        when(userService.update(userId, updateRequest))
                .thenThrow(new IllegalArgumentException("Упс! Кажется, в номере телефона ошибка. Проверьте, что номер телефона введён правильно."));

        ResponseEntity<ApiResponse> response = userController.update(userId, updateRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Упс! Кажется, в номере телефона ошибка. Проверьте, что номер телефона введён правильно.", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void update_WhenUserNotFound_ReturnsNotFound() {
        Long userId = 100L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("UpdatedName");
        updateRequest.setLastName("UpdatedLastName");
        updateRequest.setPhoneNumber("89124000000");
        when(userService.update(userId, updateRequest))
                .thenThrow(new ResourceNotFoundException("Извините, пользователь не найден"));

        ResponseEntity<ApiResponse> response = userController.update(userId, updateRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Извините, пользователь не найден", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void update_WhenInternalErrorOccurs_ReturnsInternalServerError() {
        Long userId = 4L;
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setFirstName("UpdatedName");
        updateRequest.setLastName("UpdatedLastName");
        updateRequest.setPhoneNumber("89124000000");
        doThrow(new RuntimeException()).when(userService).update(userId, updateRequest);

        ResponseEntity<ApiResponse> response = userController.update(userId, updateRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Произошла ошибка", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void getAllUsers_WhenUsersExist_ReturnsFoundStatusAndListOfUsers() {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setFirstName("Иван");
        user1.setLastName("Иванов");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setFirstName("Петр");
        user2.setLastName("Петров");

        List<UserDto> usersDto = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(usersDto);

        ResponseEntity<ApiResponse> response = userController.getAllUsers();

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals("Пользователи найдены", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(usersDto, response.getBody().getData());
    }

    @Test
    public void getAllUsers_WhenExceptionOccurs_ReturnsInternalServerError() {
        String errorMessage = "Произошла ошибка";
        when(userService.getAllUsers()).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = userController.getAllUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void register_WhenValidRequest_ReturnsSuccess() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("Иван");
        request.setLastName("Иванов");
        request.setGender("Male");
        request.setPhoneNumber("89124000000");
        request.setEmail("test@gmail.com");
        request.setPassword("TestPassword123");
        request.setUserType("VET");
        request.setSpecialization("Хирург");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName(request.getFirstName());
        savedUser.setLastName(request.getLastName());

        UserDto registeredUserDto = new UserDto();
        registeredUserDto.setId(savedUser.getId());
        registeredUserDto.setFirstName(savedUser.getFirstName());
        registeredUserDto.setLastName(savedUser.getLastName());

        String successMassage = "Учетная запись пользователя успешно создана для завершения регистрации перейдите по ссылке которая была отправлена на указанный при регистрации электронный адрес";

        when(userService.register(request)).thenReturn(savedUser);
        when(entityConverter.mapEntityToDto(savedUser, UserDto.class)).thenReturn(registeredUserDto);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        ResponseEntity<ApiResponse> response = userController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successMassage, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(registeredUserDto, response.getBody().getData());
        verify(rabbitMQProducer, times(1)).sendMessage("RegistrationCompleteEvent:1");
    }

    @Test
    public void register_WhenUserAlreadyExists_ReturnsConflict() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@gmail.com");
        String errorMessage = "Пользователь с таким email уже существует";
        when(userService.register(request)).thenThrow(new UserAlreadyExistsException(errorMessage));

        ResponseEntity<ApiResponse> response = userController.register(request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void register_WhenInvalidData_ReturnsBadRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setPassword("123");
        String errorMessage = "Пароль должен быть не менее 8 символов и содержать буквы и цифры латинского алфавита!";
        when(userService.register(request)).thenThrow(new IllegalArgumentException(errorMessage));

        ResponseEntity<ApiResponse> response = userController.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void register_WhenInternalErrorOccurs_ReturnsInternalServerError() {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@gmail.com");
        String errorMessage = "Произошла ошибка";
        when(userService.register(request)).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = userController.register(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }


    @Test
    public void changePassword_WhenValidRequest_ReturnsSuccess() {
        Long userId = 4L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("Password12345");
        request.setNewPassword("NewPassword123");
        request.setConfirmNewPassword("NewPassword123");
        doNothing().when(changePasswordService).changePassword(userId, request);

        ResponseEntity<ApiResponse> response = userController.changePassword(userId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PASSWORD_CHANGE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void changePassword_WhenCurrentPasswordWrong_ReturnsBadRequest() {
        Long userId = 4L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("WrongPassword");
        request.setNewPassword("NewPassword123");
        request.setConfirmNewPassword("NewPassword123");
        String errorMessage = "Текущий пароль указан неверно";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(changePasswordService).changePassword(userId, request);

        ResponseEntity<ApiResponse> response = userController.changePassword(userId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void changePassword_WhenUserNotFound_ReturnsNotFound() {
        Long userId = 100L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("Password12345");
        request.setNewPassword("NewPassword123");
        request.setConfirmNewPassword("NewPassword123");
        String errorMessage = "Извините, пользователь не найден";
        doThrow(new ResourceNotFoundException(errorMessage))
                .when(changePasswordService).changePassword(userId, request);

        ResponseEntity<ApiResponse> response = userController.changePassword(userId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void changePassword_InternalErrorOccurs_ReturnsInternalServerError() {
        Long userId = 4L;
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("Password12345");
        request.setNewPassword("NewPassword123");
        request.setConfirmNewPassword("NewPassword123");
        String errorMessage = "Произошла ошибка";
        doThrow(new RuntimeException(errorMessage))
                .when(changePasswordService).changePassword(userId, request);

        ResponseEntity<ApiResponse> response = userController.changePassword(userId, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void countVeterinarians_WhenCalled_ReturnsCorrectCount() {
        long expectedCount = 5L;
        when(userService.countVeterinarians()).thenReturn(expectedCount);

        long actualCount = userController.countVeterinarians();

        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void countPatients_WhenCalled_ReturnsCorrectCount() {
        long expectedCount = 4L;
        when(userService.countPatients()).thenReturn(expectedCount);

        long actualCount = userController.countPatients();

        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void countUsers_WhenCalled_ReturnsCorrectCount() {
        long expectedCount = 10L;
        when(userService.countAllUsers()).thenReturn(expectedCount);

        long actualCount = userController.countUsers();

        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void aggregateUserByMonthAndType_WhenDataExists_ReturnsAggregatedData() {
        Map<String, Map<String, Long>> aggregatedData = new HashMap<>();
        Map<String, Long> januaryData = new HashMap<>();
        januaryData.put("VET", 5L);
        januaryData.put("PATIENT", 10L);
        aggregatedData.put("January", januaryData);
        when(userService.aggregateUsersByMonthAndType()).thenReturn(aggregatedData);

        ResponseEntity<ApiResponse> response = userController.aggregateUserByMonthAndType();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(aggregatedData, response.getBody().getData());
    }

    @Test
    public void aggregateUserByMonthAndType_WhenExceptionOccurs_ReturnsInternalServerError() {
        String errorMessage = "Произошла ошибка";
        when(userService.aggregateUsersByMonthAndType()).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = userController.aggregateUserByMonthAndType();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void getAggregateUsersByEnabledStatus_WhenDataExists_ReturnsAggregatedData() {
        Map<String, Map<String, Long>> aggregateData = new HashMap<>();

        Map<String, Long> enabledData = new HashMap<>();
        enabledData.put("VET", 5L);
        enabledData.put("PATIENT", 10L);

        Map<String, Long> nonEnabledData = new HashMap<>();
        nonEnabledData.put("VET", 2L);
        nonEnabledData.put("PATIENT", 3L);

        aggregateData.put("Enabled", enabledData);
        aggregateData.put("Non-Enabled", nonEnabledData);

        when(userService.aggregateUsersByEnabledStatusAndType()).thenReturn(aggregateData);

        ResponseEntity<ApiResponse> response = userController.getAggregateUsersByEnabledStatus();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.RESOURCE_FOUND, Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(aggregateData, response.getBody().getData());
    }

    @Test
    public void getAggregateUsersByEnabledStatus_WhenExceptionOccurs_ReturnsInternalServerError() {
        String errorMessage = "Произошла ошибка";
        when(userService.aggregateUsersByEnabledStatusAndType()).thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<ApiResponse> response = userController.getAggregateUsersByEnabledStatus();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void lockUserAccount_WhenSuccess_ReturnsSuccessMessage() {
        Long userId = 5L;
        doNothing().when(userService).lockUserAccount(userId);

        ResponseEntity<ApiResponse> response = userController.lockUserAccount(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Учетная запись успешно заблокирована", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void lockUserAccount_WhenExceptionOccurs_ReturnsInternalServerError() {
        Long userId = 5L;
        String errorMessage = "Произошла ошибка";
        doThrow(new RuntimeException(errorMessage)).when(userService).lockUserAccount(userId);

        ResponseEntity<ApiResponse> response = userController.lockUserAccount(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void unLockUserAccount_WhenSuccess_ReturnsSuccessMessage() {
        Long userId = 6L;
        doNothing().when(userService).unLockUserAccount(userId);

        ResponseEntity<ApiResponse> response = userController.unLockUserAccount(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Учетная запись успешно разблокирована", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void unLockUserAccount_WhenExceptionOccurs_ReturnsInternalServerError() {
        Long userId = 6L;
        String errorMessage = "Произошла ошибка";
        doThrow(new RuntimeException(errorMessage)).when(userService).unLockUserAccount(userId);

        ResponseEntity<ApiResponse> response = userController.unLockUserAccount(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(errorMessage, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    public void getPhotoByUserId_WhenUserExists_ReturnsPhoto() {
        Long userId = 4L;
        byte[] photoBytes = "fake-image-data".getBytes();
        when(userService.getPhotoByUserId(userId)).thenReturn(photoBytes);

        ResponseEntity<byte[]> response = userController.getPhotoByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertEquals(photoBytes, response.getBody());
    }

    @Test
    public void getPhotoByUserId_WhenUserNotFound_ThrowsResourceNotFoundException() {
        Long userId = 100L;
        when(userService.getPhotoByUserId(userId)).thenThrow(new ResourceNotFoundException(""));

        ResponseEntity<byte[]> response = userController.getPhotoByUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getPhotoByUserId_WhenInternalServerError_ThrowsException() {
        Long userId = 4L;
        when(userService.getPhotoByUserId(userId)).thenThrow(new RuntimeException());

        ResponseEntity<byte[]> response = userController.getPhotoByUserId(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
