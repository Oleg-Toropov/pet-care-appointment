package com.olegtoropoff.petcareappointment.service.user;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.factory.UserFactory;
import com.olegtoropoff.petcareappointment.model.Photo;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.request.UserUpdateRequest;
import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import com.olegtoropoff.petcareappointment.service.review.IReviewService;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private IAppointmentService appointmentService;

    @Spy
    private EntityConverter<User, UserDto> entityConverter = new EntityConverter<>(new ModelMapper());

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private IVerificationTokenService tokenService;

    @Mock
    private IReviewService reviewService;

    @Test
    void register_WhenValid_ReturnsUser() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("test@gmail.com");
        request.setPassword("Password12345");
        request.setPhoneNumber("89124000000");

        User user = new User();
        when(userFactory.createUser(request)).thenReturn(user);

        UserDto result = userService.register(request);

        assertNotNull(result);
        verify(tokenService, times(1)).saveVerificationTokenForUser(anyString(), eq(user));
    }

    @Test
    void register_WhenInvalidFirstName_ThrowsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("John12");
        request.setLastName("Doe");
        request.setEmail("test@gmail.com");
        request.setPassword("Password12345");
        request.setPhoneNumber("89124000000");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        assertEquals(FeedBackMessage.INVALID_NAME_FORMAT, exception.getMessage());
    }

    @Test
    void register_WhenInvalidPassword_ThrowsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("test@gmail.com");
        request.setPassword("Password");
        request.setPhoneNumber("89124000000");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        assertEquals(FeedBackMessage.INVALID_PASSWORD_FORMAT, exception.getMessage());
    }

    @Test
    void register_WhenInvalidEmail_ThrowsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("test@Invalid.com");
        request.setPassword("Password12345");
        request.setPhoneNumber("89124000000");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        assertEquals(FeedBackMessage.INVALID_EMAIL_FORMAT, exception.getMessage());
    }

    @Test
    void register_WhenInvalidPhone_ThrowsException() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("test@gmail.com");
        request.setPassword("Password12345");
        request.setPhoneNumber("124000000");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        assertEquals(FeedBackMessage.INVALID_PHONE_FORMAT, exception.getMessage());
    }

    @Test
    void update_WhenValidRequest_UpdatesAndReturnsUser() {
        Long userId = 1L;
        String firstName = "John";
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName(firstName);
        userUpdateRequest.setLastName("Doe");
        userUpdateRequest.setPhoneNumber("89124000000");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName(firstName);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto result = userService.update(userId, userUpdateRequest);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(firstName, result.getFirstName());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void findById_WhenUserExists_ReturnsUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findById_WhenUserDoesNotExist_ThrowsException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.findById(userId));
        assertEquals(FeedBackMessage.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void deleteById_WhenUserExists_DeletesUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
        verify(reviewRepository, times(1)).deleteAll(anyList());
        verify(appointmentRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void deleteById_WhenUserDoesNotExist_ThrowsException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(userId));
        assertEquals(FeedBackMessage.USER_NOT_FOUND, exception.getMessage());
    }

    private User createUser(Long userId) {
        User user = new User();
        user.setId(userId);

        Photo photo = createPhoto(2L);
        user.setPhoto(photo);

        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    private Photo createPhoto(Long photoId) {
        Photo photo = new Photo();
        photo.setId(photoId);
        return photo;
    }

    @Test
    void getUserWithDetails_WhenValidUserId_ReturnsUserDto() {
        Long userId = 1L;
        User user = createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(appointmentService.getUserAppointments(userId)).thenReturn(Collections.emptyList());
        when(reviewService.findAllReviewsByUserId(userId)).thenReturn(Collections.emptyList());

        UserDto result = userService.getUserWithDetails(userId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(userId);
        verify(entityConverter, times(1)).mapEntityToDto(user, UserDto.class);
        verify(appointmentService, times(1)).getUserAppointments(userId);
    }
}
