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
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public User register(RegistrationRequest request) {
        return userFactory.createUser(request);
    }

    @Override
    public User update(Long userId, UserUpdateRequest request) {
        User user = findById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setSpecialization(request.getSpecialization());
        return userRepository.save(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> entityConverter.mapEntityToDto(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
    }

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

    private void setUserAppointments(UserDto userDto) {
        List<AppointmentDto> appointmentDtos = appointmentService.getUserAppointments(userDto.getId());
        userDto.setAppointments(appointmentDtos);
    }

    private void setUserPhoto(UserDto userDto, User user) throws SQLException {
        if (user.getPhoto() != null) {
            userDto.setPhotoId(user.getPhoto().getId());
            userDto.setPhoto(photoService.getImageData(user.getPhoto().getId()));
        }
    }

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

    private ReviewDto mapReviewToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(review.getId());
        reviewDto.setStars(review.getStars());
        reviewDto.setFeedback(review.getFeedback());
        mapVeterinarianInfo(reviewDto, review);
        mapPatientInfo(reviewDto, review);
        return reviewDto;
    }

    private void mapVeterinarianInfo(ReviewDto reviewDto, Review review) {
        if (review.getVeterinarian() != null) {
            reviewDto.setVeterinarianId(review.getVeterinarian().getId());
            reviewDto.setVeterinarianName(review.getVeterinarian().getFirstName() + " " + review.getVeterinarian().getLastName());
            setVeterinarianPhoto(reviewDto, review);
        }
    }

    private void mapPatientInfo(ReviewDto reviewDto, Review review) {
        if (review.getPatient() != null) {
            reviewDto.setPatientId(review.getPatient().getId());
            reviewDto.setPatientName(review.getPatient().getFirstName() + " " + review.getPatient().getLastName());
            setReviewerPhoto(reviewDto, review);
        }
    }

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

    @Override
    public long countVeterinarians() {
        return userRepository.countByUserType("VET");
    }

    @Override
    public long countPatients() {
        return userRepository.countByUserType("PATIENT");
    }

    @Override
    public long countAllUsers() {
        return userRepository.count();
    }

    @Override
    public Map<String, Map<String, Long>> aggregateUsersByMonthAndType() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(user -> Month.of(user.getCreatedAt().getMonthValue())
                                .getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        Collectors.groupingBy(User::getUserType, Collectors.counting())
                ));
    }

    @Override
    public Map<String, Map<String, Long>> aggregateUsersByEnabledStatusAndType(){
        List<User> users = userRepository.findAll();
        return users.stream()
                .collect(Collectors.groupingBy(user -> user.isEnabled()? "Enabled" : "Non-Enabled",
                        Collectors.groupingBy(User :: getUserType, Collectors.counting())));
    }

    @Override
    public void lockUserAccount(Long userId) {
        userRepository.updateUserEnabledStatus(userId, false);
    }

    @Override
    public void unLockUserAccount(Long userId) {
        userRepository.updateUserEnabledStatus(userId, true);
    }
}
