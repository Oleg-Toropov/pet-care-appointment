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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

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
     * @return the registered {@link UserDto}.
     */
    @Override
    public UserDto register(RegistrationRequest request) {
        validateRegistrationRequest(request);
        User user = userFactory.createUser(request);
        String vToken = UUID.randomUUID().toString();
        tokenService.saveVerificationTokenForUser(vToken, user);
        return entityConverter.mapEntityToDto(user, UserDto.class);
    }

    /**
     * Updates an existing user's details.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Validates the update request.</li>
     *     <li>Finds the user by their ID.</li>
     *     <li>Maps the update request to the existing user entity.</li>
     *     <li>Saves the updated user to the database.</li>
     *     <li>Converts the updated entity to a {@link UserDto} and returns it.</li>
     * </ul>
     * <p>
     * <b>Cache Eviction:</b>
     * <ul>
     *     <li>Clears `veterinarians_with_details` and `specializations` caches to ensure the updated user data is reflected.</li>
     * </ul>
     *
     * @param userId  the ID of the user to update.
     * @param request the update request containing new user details.
     * @return the updated {@link UserDto} with the modified user details.
     * @throws ResourceNotFoundException if the user does not exist.
     * @throws IllegalArgumentException if the update request is invalid.
     */
    @CacheEvict(value = {"veterinarians_with_details", "specializations"}, allEntries = true)
    @Override
    public UserDto update(Long userId, UserUpdateRequest request) {
        validateUserUpdateRequest(request);
        User user = findById(userId);
        mapUserUpdateRequestToUser(request, user);
        User updatedUser = userRepository.save(user);
        return entityConverter.mapEntityToDto(updatedUser, UserDto.class);
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
     * Deletes a user by their ID along with all related data.
     * <p>
     * This method performs the following operations:
     * <ul>
     *     <li>Finds the user by their ID.</li>
     *     <li>Deletes all reviews associated with the user.</li>
     *     <li>Deletes all appointments linked to the user.</li>
     *     <li>If the user has an associated photo, deletes the photo.</li>
     *     <li>Removes the user from the database.</li>
     * </ul>
     * <p>
     * <b>Cache Eviction:</b>
     * <ul>
     *     <li>Clears `veterinarians_with_details`, `specializations`, `veterinarian_ratings`, and `user_reviews` caches.</li>
     *     <li>Removes the `veterinarian_biography` entry for the deleted user.</li>
     * </ul>
     *
     * @param userId the ID of the user to delete.
     * @throws ResourceNotFoundException if the user is not found.
     */
    @Caching(evict = {
            @CacheEvict(value = "veterinarians_with_details", allEntries = true),
            @CacheEvict(value = "specializations", allEntries = true),
            @CacheEvict(value = "veterinarian_ratings", allEntries = true),
            @CacheEvict(value = "user_reviews", allEntries = true),
            @CacheEvict(value = "veterinarian_biography", key = "#userId")
    })
    @Override
    public void deleteById(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(userToDelete -> {
                    List<Review> reviews = new ArrayList<>(reviewRepository.findAllByUserId(userId));
                    reviewRepository.deleteAll(reviews);
                    List<Appointment> appointments = new ArrayList<>(appointmentRepository.findAllByUserId(userId));
                    appointmentRepository.deleteAll(appointments);
                    if (userToDelete.getPhoto() != null) {
                        photoService.deletePhoto(userToDelete.getPhoto().getId(), userId);
                    }
                    userRepository.deleteById(userId);
                }, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND);
                });
    }

    /**
     * Retrieves a user along with their detailed information, including appointments, reviews, and photo URL.
     *
     * @param userId the ID of the user to retrieve.
     * @return a {@link UserDto} containing user details, including links to photos, appointments, and reviews.
     */
    @Override
    public UserDto getUserWithDetails(Long userId) {
        User user = findById(userId);
        UserDto userDto = entityConverter.mapEntityToDto(user, UserDto.class);
        setUserAppointments(userDto);
        populateUserReviewDetails(userDto, userId);
        return userDto;
    }

    /**
     * Populates a {@link UserDto} with review details for the given user.
     * <p>
     * This method:
     * <ul>
     *     <li>Retrieves all reviews associated with the user.</li>
     *     <li>Maps the reviews to {@link ReviewDto} objects.</li>
     *     <li>Calculates and sets the average rating for the user (if they have reviews).</li>
     *     <li>Sets the total number of reviewers.</li>
     *     <li>Attaches the mapped reviews to the {@link UserDto}.</li>
     * </ul>
     *
     * @param userDto the {@link UserDto} to populate with review details.
     * @param userId the ID of the user whose review details should be populated.
     */
    @Override
    public void populateUserReviewDetails(UserDto userDto, Long userId) {
        List<Review> review = reviewService.findAllReviewsByUserId(userId);
        List<ReviewDto> reviewDto = review.stream()
                .map(this::mapReviewToDto).toList();
        if (!reviewDto.isEmpty()) {
            double averageRating = getAverageRatingForVet(reviewDto);
            userDto.setAverageRating(averageRating);
            userDto.setTotalReviewers((long) reviewDto.size());
        }
        userDto.setReviews(reviewDto);
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
     * where the key is the user type and the value is the count of users.
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
     * and the value is another map where the key is the user type and the value
     * is the count of users.
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
     * <p>
     * This method updates the user's `enabled` status to `false` in the database, effectively locking the account.
     * The operation is typically used for administrative actions such as suspending a veterinarian.
     * <p>
     * <b>Cache Eviction:</b>
     * - Clears `veterinarians_with_details` cache to ensure that locked users do not appear in cached lists.
     *
     * @param userId the ID of the user whose account will be locked.
     */
    @CacheEvict(value = {"veterinarians_with_details"}, allEntries = true)
    @Override
    public void lockUserAccount(Long userId) {
        userRepository.updateUserEnabledStatus(userId, false);
    }

    /**
     * Unlocks the user's account by enabling their access.
     * <p>
     * This method updates the user's `enabled` status to `true` in the database, allowing them to regain access.
     * This operation is typically used for reactivating suspended accounts.
     * <p>
     * <b>Cache Eviction:</b>
     * - Clears `veterinarians_with_details` cache to ensure that unlocked users are visible in cached lists.
     *
     * @param userId the ID of the user whose account will be unlocked.
     */
    @CacheEvict(value = {"veterinarians_with_details"}, allEntries = true)
    @Override
    public void unLockUserAccount(Long userId) {
        userRepository.updateUserEnabledStatus(userId, true);
    }

    /**
     * Retrieves the URL of the photo associated with a specific user.
     * If the user has an associated photo, this method returns the URL of the photo stored in the S3 bucket.
     * If the user does not have a photo, it returns {@code null}.
     *
     * @param userId the ID of the user whose photo URL is to be retrieved.
     * @return the URL of the user's photo, or {@code null} if the user has no associated photo.
     * @throws ResourceNotFoundException if the user with the specified ID does not exist.
     */
    @Override
    public String getPhotoUrlByUserId(Long userId) {
        User user = findById(userId);
        if (user.getPhoto() != null) {
            return photoService.getPhotoUrlById(user.getPhoto().getId());
        }
        return null;
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
     * This method sets the veterinarian's ID, full name, and photo URL in the given {@link ReviewDto}.
     *
     * @param reviewDto the {@link ReviewDto} where veterinarian information will be set.
     * @param review    the {@link Review} containing veterinarian details, including ID, name, and photo URL.
     */
    private void mapVeterinarianInfo(ReviewDto reviewDto, Review review) {
        if (review.getVeterinarian() != null) {
            reviewDto.setVeterinarianId(review.getVeterinarian().getId());
            reviewDto.setVeterinarianName(review.getVeterinarian().getFirstName() + " " + review.getVeterinarian().getLastName());
            if (review.getVeterinarian().getPhoto() != null) {
                reviewDto.setVeterinarianImageUrl(review.getVeterinarian().getPhoto().getS3Url());
            }
        }
    }

    /**
     * Maps patient information from a {@link Review} to a {@link ReviewDto}.
     * This method sets the patient's ID, full name, and photo URL in the given {@link ReviewDto}.
     *
     * @param reviewDto the {@link ReviewDto} where patient information will be set.
     * @param review    the {@link Review} containing patient details, including ID, name, and photo URL.
     */
    private void mapPatientInfo(ReviewDto reviewDto, Review review) {
        if (review.getPatient() != null) {
            reviewDto.setPatientId(review.getPatient().getId());
            reviewDto.setPatientName(review.getPatient().getFirstName() + " " + review.getPatient().getLastName());
            if (review.getPatient().getPhoto() != null) {
                reviewDto.setPatientImageUrl(review.getPatient().getPhoto().getS3Url());
            }
        }
    }

    /**
     * Calculates the average rating from a list of {@link ReviewDto}.
     * <p>
     * This method processes a list of {@link ReviewDto} objects, extracts the {@code stars} field,
     * and computes the average value. If the list is empty, it returns {@code 0.0}.
     *
     * @param reviewDto the list of {@link ReviewDto} objects containing review details.
     * @return the average rating as a {@code double}, or {@code 0.0} if the list is empty.
     */
    private double getAverageRatingForVet(List<ReviewDto> reviewDto) {
        return reviewDto.stream()
                .mapToInt(ReviewDto::getStars)
                .average()
                .orElse(0.0);
    }
}
