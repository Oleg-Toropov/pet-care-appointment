package com.olegtoropoff.petcareappointment.service.user;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.ReviewDto;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.factory.UserFactory;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.request.UserUpdateRequest;
import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import com.olegtoropoff.petcareappointment.service.photo.IPhotoService;
import com.olegtoropoff.petcareappointment.service.review.IReviewService;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.validation.EmailValidator;
import com.olegtoropoff.petcareappointment.validation.NameValidator;
import com.olegtoropoff.petcareappointment.validation.PasswordValidator;
import com.olegtoropoff.petcareappointment.validation.PhoneValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for handling user-related operations.
 * Provides functionality for registering, updating, retrieving, and managing user data,
 * including roles, appointments, and reviews.
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final IAppointmentService appointmentService;
    private final IPhotoService photoService;
    private final IReviewService reviewService;
    private final EntityConverter<User, UserDto> entityConverter;
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final IVerificationTokenService tokenService;

    /**
     * Registers a new user based on the provided registration request.
     * Validates the request and generates a verification token for the user.
     *
     * @param request the registration request containing user details.
     * @return the registered {@link User}.
     */
    @Override
    public User register(RegistrationRequest request) {
        validateRegistrationRequest(request);
        User user = userFactory.createUser(request);
        String vToken = UUID.randomUUID().toString();
        tokenService.saveVerificationTokenForUser(vToken, user);
        return user;
    }

    /**
     * Updates an existing user's details.
     * Validates the update request and updates the user in the database.
     *
     * @param userId  the ID of the user to update.
     * @param request the update request containing new user details.
     * @return the updated {@link User}.
     */
    @Override
    public User update(Long userId, UserUpdateRequest request) {
        validateUserUpdateRequest(request);
        User user = findById(userId);
        mapUserUpdateRequestToUser(request, user);
        return userRepository.save(user);
    }

    /**
     * Retrieves a list of all users.
     *
     * @return a list of {@link UserDto} objects representing all users.
     */
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> entityConverter.mapEntityToDto(user, UserDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve.
     * @return the {@link User} with the specified ID.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
    }

    /**
     * Deletes a user by their ID.
     * Also deletes all related reviews and appointments.
     *
     * @param userId the ID of the user to delete.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Override
    public void deleteById(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(userToDelete -> {
                    List<Review> reviews = new ArrayList<>(reviewRepository.findAllByUserId(userId));
                    reviewRepository.deleteAll(reviews);
                    List<Appointment> appointments = new ArrayList<>(appointmentRepository.findAllByUserId(userId));
                    appointmentRepository.deleteAll(appointments);
                    userRepository.deleteById(userId);
                }, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND);
                });
    }

    /**
     * Retrieves a user along with their detailed information, including appointments, reviews, and photos.
     *
     * @param userId the ID of the user to retrieve.
     * @return a {@link UserDto} containing user details.
     * @throws SQLException if an error occurs while retrieving photo data.
     */
    @Override
    public UserDto getUserWithDetails(Long userId) throws SQLException {
        User user = findById(userId);
        UserDto userDto = entityConverter.mapEntityToDto(user, UserDto.class);
        userDto.setTotalReviewers(reviewRepository.countByVeterinarianId(userId));
        setUserAppointments(userDto);
        setUserPhoto(userDto, user);
        setUserReviews(userDto, userId);
        return userDto;
    }

    /**
     * Validates the registration request to ensure all required fields are valid.
     * Formats the first name and last name to have proper capitalization.
     *
     * @param request the {@link RegistrationRequest} containing user details for registration.
     * @throws IllegalArgumentException if any field in the request is invalid.
     */
    private void validateRegistrationRequest(RegistrationRequest request) {
        if (!NameValidator.isValid(request.getFirstName()) || !NameValidator.isValid(request.getLastName())) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_NAME_FORMAT);
        }
        if (!PasswordValidator.isValid(request.getPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_PASSWORD_FORMAT);
        }
        if (!EmailValidator.isValid(request.getEmail())) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_EMAIL_FORMAT);
        }
        if (!PhoneValidator.isValid(request.getPhoneNumber())) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_PHONE_FORMAT);
        }

        request.setFirstName(NameValidator.format(request.getFirstName()));
        request.setLastName(NameValidator.format(request.getLastName()));
    }

    /**
     * Validates the user update request to ensure all required fields are valid.
     * Formats the first name and last name to have proper capitalization.
     *
     * @param request the {@link UserUpdateRequest} containing updated user details.
     * @throws IllegalArgumentException if any field in the request is invalid.
     */
    private void validateUserUpdateRequest(UserUpdateRequest request) {
        if (!NameValidator.isValid(request.getFirstName()) || !NameValidator.isValid(request.getLastName())) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_NAME_FORMAT);
        }
        if (!PhoneValidator.isValid(request.getPhoneNumber())) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_PHONE_FORMAT);
        }

        request.setFirstName(NameValidator.format(request.getFirstName()));
        request.setLastName(NameValidator.format(request.getLastName()));
    }

    /**
     * Maps the user update request to an existing user entity.
     *
     * @param request the {@link UserUpdateRequest} containing updated user details.
     * @param user    the existing {@link User} entity to be updated.
     */
    private void mapUserUpdateRequestToUser(UserUpdateRequest request, User user) {
        user.setFirstName(NameValidator.format(request.getFirstName()));
        user.setLastName(NameValidator.format(request.getLastName()));
        user.setGender(request.getGender());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setSpecialization(request.getSpecialization());
    }

    /**
     * Sets the appointments for the given user DTO by fetching appointment details.
     *
     * @param userDto the {@link UserDto} object where appointments will be set.
     */
    private void setUserAppointments(UserDto userDto) {
        List<AppointmentDto> appointmentDtos = appointmentService.getUserAppointments(userDto.getId());
        userDto.setAppointments(appointmentDtos);
    }

    /**
     * Sets the photo details for the given user DTO by fetching the photo data.
     *
     * @param userDto the {@link UserDto} object where the photo will be set.
     * @param user    the {@link User} whose photo data will be fetched.
     * @throws SQLException if there is an error accessing the database.
     */
    private void setUserPhoto(UserDto userDto, User user) throws SQLException {
        if (user.getPhoto() != null) {
            userDto.setPhotoId(user.getPhoto().getId());
            userDto.setPhoto(photoService.getImageData(user.getPhoto().getId()));
        }
    }

    /**
     * Sets the reviews for the given user DTO by fetching and mapping review details.
     *
     * @param userDto the {@link UserDto} object where reviews will be set.
     * @param userId  the ID of the user whose reviews will be fetched.
     */
    private void setUserReviews(UserDto userDto, Long userId) {
        Page<Review> reviewPage = reviewService.findAllReviewsByUserId(userId, 0, Integer.MAX_VALUE);
        List<ReviewDto> reviewDto = reviewPage.getContent()
                .stream()
                .map(this::mapReviewToDto).toList();
        if (!reviewDto.isEmpty()) {
            double averageRating = reviewService.getAverageRatingForVet(userId);
            userDto.setAverageRating(averageRating);
        }
        userDto.setReviews(reviewDto);
    }

    /**
     * Maps a {@link Review} entity to a {@link ReviewDto}.
     *
     * @param review the {@link Review} entity to be mapped.
     * @return a {@link ReviewDto} containing mapped review details.
     */
    private ReviewDto mapReviewToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setStars(review.getStars());
        reviewDto.setFeedback(review.getFeedback());
        mapVeterinarianInfo(reviewDto, review);
        mapPatientInfo(reviewDto, review);
        return reviewDto;
    }

    /**
     * Maps veterinarian information from a {@link Review} to a {@link ReviewDto}.
     *
     * @param reviewDto the {@link ReviewDto} where veterinarian information will be set.
     * @param review    the {@link Review} containing veterinarian details.
     */
    private void mapVeterinarianInfo(ReviewDto reviewDto, Review review) {
        if (review.getVeterinarian() != null) {
            reviewDto.setVeterinarianId(review.getVeterinarian().getId());
            reviewDto.setVeterinarianName(review.getVeterinarian().getFirstName() + " " + review.getVeterinarian().getLastName());
            setVeterinarianPhoto(reviewDto, review);
        }
    }

    /**
     * Maps patient information from a {@link Review} to a {@link ReviewDto}.
     *
     * @param reviewDto the {@link ReviewDto} where patient information will be set.
     * @param review    the {@link Review} containing patient details.
     */
    private void mapPatientInfo(ReviewDto reviewDto, Review review) {
        if (review.getPatient() != null) {
            reviewDto.setPatientId(review.getPatient().getId());
            reviewDto.setPatientName(review.getPatient().getFirstName() + " " + review.getPatient().getLastName());
            setReviewerPhoto(reviewDto, review);
        }
    }

    /**
     * Sets the reviewer's photo for a given review DTO by fetching photo data.
     *
     * @param reviewDto the {@link ReviewDto} where the patient's photo will be set.
     * @param review    the {@link Review} containing patient photo details.
     */
    private void setReviewerPhoto(ReviewDto reviewDto, Review review) {
        if (review.getPatient().getPhoto() != null) {
            try {
                reviewDto.setPatientImage(photoService.getImageData(review.getPatient().getPhoto().getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            reviewDto.setPatientImage(null);
        }
    }

    /**
     * Sets the veterinarian's photo for a given review DTO by fetching photo data.
     *
     * @param reviewDto the {@link ReviewDto} where the veterinarian's photo will be set.
     * @param review    the {@link Review} containing veterinarian photo details.
     */
    private void setVeterinarianPhoto(ReviewDto reviewDto, Review review) {
        if (review.getVeterinarian().getPhoto() != null) {
            try {
                reviewDto.setVeterinarianImage(photoService.getImageData(review.getVeterinarian().getPhoto().getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            reviewDto.setVeterinarianImage(null);
        }
    }

    /**
     * Counts the total number of users with the role of "VET".
     *
     * @return the total count of veterinarians in the system.
     */
    @Override
    public long countVeterinarians() {
        return userRepository.countByUserType("VET");
    }

    /**
     * Counts the total number of users with the role of "PATIENT".
     *
     * @return the total count of patients in the system.
     */
    @Override
    public long countPatients() {
        return userRepository.countByUserType("PATIENT");
    }

    /**
     * Counts the total number of users in the system.
     *
     * @return the total count of all users.
     */
    @Override
    public long countAllUsers() {
        return userRepository.count();
    }

    /**
     * Aggregates the count of users by their creation month and user type.
     *
     * @return a nested map where the key is the month, the value is another map
     *         where the key is the user type and the value is the count of users.
     */
    @Override
    public Map<String, Map<String, Long>> aggregateUsersByMonthAndType() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(user -> Month.of(user.getCreatedAt().getMonthValue())
                                .getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        Collectors.groupingBy(User::getUserType, Collectors.counting())
                ));
    }

    /**
     * Aggregates the count of users by their enabled status and user type.
     *
     * @return a nested map where the key is the enabled status ("Enabled" or "Non-Enabled"),
     *         and the value is another map where the key is the user type and the value
     *         is the count of users.
     */
    @Override
    public Map<String, Map<String, Long>> aggregateUsersByEnabledStatusAndType() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(user -> user.isEnabled() ? "Enabled" : "Non-Enabled",
                        Collectors.groupingBy(User::getUserType, Collectors.counting())));
    }

    /**
     * Locks the user's account by disabling their access.
     *
     * @param userId the ID of the user whose account will be locked.
     */
    @Override
    public void lockUserAccount(Long userId) {
        userRepository.updateUserEnabledStatus(userId, false);
    }

    /**
     * Unlocks the user's account by enabling their access.
     *
     * @param userId the ID of the user whose account will be unlocked.
     */
    @Override
    public void unLockUserAccount(Long userId) {
        userRepository.updateUserEnabledStatus(userId, true);
    }

    /**
     * Retrieves the photo data for a given user's photo by user ID.
     *
     * @param userId the ID of the user whose photo data will be fetched.
     * @return a byte array containing the photo data, or null if no photo exists.
     */
    @Override
    public byte[] getPhotoByUserId(Long userId) {
        User user = findById(userId);
        if (user.getPhoto() != null) {
            try {
                return photoService.getImageData(user.getPhoto().getId());
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return null;
    }
}
