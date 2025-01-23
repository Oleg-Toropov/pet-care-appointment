package com.olegtoropoff.petcareappointment.service.user;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.factory.UserFactory;
import com.olegtoropoff.petcareappointment.model.*;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.request.UserUpdateRequest;
import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import com.olegtoropoff.petcareappointment.service.review.IReviewService;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

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

    @Mock
    private EntityConverter<User, UserDto> entityConverter;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private IVerificationTokenService tokenService;

    @Mock
    private IReviewService reviewService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

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

        User result = userService.register(request);

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
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("89124000000");
        request.setGender("Male");

        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.update(userId, request);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("89124000000", result.getPhoneNumber());
        assertEquals("Male", result.getGender());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void getAllUsers_ReturnsUserDtoList() {
        User user = new User();
        UserDto userDto = new UserDto();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(entityConverter.mapEntityToDto(user, UserDto.class)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userRepository, times(1)).findAll();
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

    private Review createReview(Long reviewId, User patient, User veterinarian, int stars) {
        Review review = new Review();
        review.setId(reviewId);
        review.setPatient(patient instanceof Patient ? patient : null);
        review.setVeterinarian(veterinarian instanceof Veterinarian ? veterinarian : null);
        review.setStars(stars);
        return review;
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
        UserDto userDto = new UserDto();
        userDto.setId(userId);

        Patient patient = new Patient();
        patient.setPhoto(createPhoto(3L));

        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setPhoto(createPhoto(4L));

        Review review1 = createReview(1L, patient, null, 4);
        Review review2 = createReview(2L, null, veterinarian, 2);

        List<Review> reviews = List.of(review1, review2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(entityConverter.mapEntityToDto(user, UserDto.class)).thenReturn(userDto);
        when(appointmentService.getUserAppointments(userId)).thenReturn(Collections.emptyList());
        when(reviewService.findAllReviewsByUserId(userId)).thenReturn(reviews);


        UserDto result = userService.getUserWithDetails(userId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2L, result.getTotalReviewers());
        assertEquals(3, result.getAverageRating());
        verify(userRepository, times(1)).findById(userId);
        verify(entityConverter, times(1)).mapEntityToDto(user, UserDto.class);
        verify(appointmentService, times(1)).getUserAppointments(userId);
    }
}
