package com.olegtoropoff.petcareappointment.service.veterinarian;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.projection.VeterinarianReviewProjection;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.service.review.IReviewService;
import com.olegtoropoff.petcareappointment.service.user.IUserService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.olegtoropoff.petcareappointment.enums.AppointmentStatus.CANCELLED;
import static com.olegtoropoff.petcareappointment.enums.AppointmentStatus.NOT_APPROVED;

/**
 * Service layer for managing operations related to veterinarians.
 * Provides functionality to fetch, aggregate, and validate veterinarians and their appointments.
 */
@Service
@RequiredArgsConstructor
public class VeterinarianService implements IVeterinarianService {
    /**
     * The duration of an appointment in minutes.
     * Used to define the standard time allocated for each appointment.
     */
    public static final int APPOINTMENT_DURATION_MINUTES = 45;

    /**
     * The buffer time in minutes before the start of an appointment during which the slot is considered unavailable.
     */
    private static final int UNAVAILABLE_BEFORE_START_MINUTES = 10;

    /**
     * The buffer time in minutes after the end of an appointment during which the slot is considered unavailable.
     */
    private static final int UNAVAILABLE_AFTER_END_MINUTES = 10;

    /**
     * The start time of the working day.
     * Defines the earliest time appointments can be scheduled.
     */
    private static final LocalTime BEGINNING_OF_WORKING_DAY = LocalTime.of(9, 0);

    /**
     * The end time of the working day.
     * Defines the latest time appointments can be scheduled.
     */
    private static final LocalTime END_OF_WORKING_DAY = LocalTime.of(21, 0);

    /**
     * The buffer time in minutes before the end of the working day during which appointments cannot be booked.
     */
    private static final int UNAVAILABLE_BEFORE_END_OF_WORKING_DAY = 55;

    /**
     * The duration in minutes of the available time period for booking an appointment.
     */
    private static final int AVAILABLE_PERIOD_FOR_BOOK_APPOINTMENT = 30;

    /**
     * The minimum number of hours from the current time required to book an appointment.
     * Ensures that appointments are not booked too close to the current time.
     */
    private static final int MINIMUM_HOURS_FROM_NOW_FOR_APPOINTMENT = 2;

    private final EntityConverter<Veterinarian, UserDto> entityConverter;
    private final IReviewService reviewService;
    private final AppointmentRepository appointmentRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final IUserService userService;


    /**
     * Retrieves a list of all enabled veterinarians with detailed information.
     * This method fetches veterinarians with the user type "VET" and {@code isEnabled} status set to {@code true},
     * then maps them to {@link UserDto} objects. During the mapping process, additional details such as
     * average rating and total number of reviews for each veterinarian are included.
     * <p>
     * The method leverages caching to store the resulting list of enabled veterinarians for faster subsequent access.
     * Caching is controlled by the {@link Cacheable} annotation, where the cache key is "veterinarians_with_details".
     * <p>
     * Cache details:
     * <ul>
     *   <li>Cache name: "veterinarians_with_details"</li>
     *   <li>Cache entry is skipped if the result is {@code null} or empty.</li>
     * </ul>
     *
     * @return a list of {@link UserDto} representing enabled veterinarians with detailed information,
     * including their average ratings and total review counts.
     */
    @Cacheable(value = "veterinarians_with_details", unless = "#result == null or #result.isEmpty()")
    @Override
    public List<UserDto> getAllVeterinariansWithDetails() {
        List<Veterinarian> veterinarians = veterinarianRepository.findAllByUserTypeAndIsEnabled("VET", true);
        Map<Long, VeterinarianReviewProjection> statsMap = reviewService.getAverageRatingsAndTotalReviews();
        return veterinarians.stream()
                .map(vet -> mapVeterinarianToUserDto(vet, statsMap))
                .toList();
    }

    /**
     * Retrieves a veterinarian's details along with their photo and reviews.
     * <p>
     * This method:
     * <ul>
     *     <li>Fetches a veterinarian by their ID, including their photo.</li>
     *     <li>Throws an exception if the veterinarian is not found.</li>
     *     <li>Converts the veterinarian entity into a {@link UserDto}.</li>
     *     <li>Populates the DTO with review details, including average rating and total reviews.</li>
     * </ul>
     *
     * @param vetId the ID of the veterinarian.
     * @return the {@link UserDto} containing veterinarian details, photo, and reviews.
     * @throws ResourceNotFoundException if the veterinarian is not found.
     */
    @Override
    public UserDto getVeterinarianWithDetailsAndReview(Long vetId) {
        Veterinarian veterinarian = veterinarianRepository.findVeterinarianWithPhotoById(vetId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.VETERINARIAN_NOT_FOUND));
        UserDto userDto = entityConverter.mapEntityToDto(veterinarian, UserDto.class);
        userService.populateUserReviewDetails(userDto, vetId);
        return userDto;
    }

    /**
     * Retrieves all available specializations for veterinarians.
     * This method fetches a list of unique specialization names from the database
     * by querying the {@link VeterinarianRepository}.
     * <p>
     * The method leverages caching to improve performance by storing the result under the cache name "specializations".
     * This reduces database queries for subsequent calls.
     * <p>
     * Cache details:
     * <ul>
     *   <li>Cache name: "specializations"</li>
     *   <li>Cache entry is skipped if the result is {@code null} or empty.</li>
     * </ul>
     *
     * @return a {@link List} of {@link String} representing all unique specializations available for veterinarians.
     * The list is empty if no specializations are found.
     */
    @Cacheable(value = "specializations", unless = "#result == null or #result.isEmpty()")
    @Override
    public List<String> getSpecializations() {
        return veterinarianRepository.getSpecializations();
    }

    /**
     * Finds available veterinarians based on specialization, date, and time.
     *
     * @param specialization the specialization to filter veterinarians.
     * @param date           the date for the appointment.
     * @param time           the time for the appointment.
     * @return a list of {@link UserDto} representing available veterinarians.
     */
    @Override
    public List<UserDto> findAvailableVeterinariansForAppointments(String specialization, LocalDate date, LocalTime time) {
        List<Veterinarian> filteredVets = getAvailableVeterinarians(specialization, date, time);
        Map<Long, VeterinarianReviewProjection> statsMap = reviewService.getAverageRatingsAndTotalReviews();
        return filteredVets.stream()
                .map(vet -> mapVeterinarianToUserDto(vet, statsMap))
                .toList();
    }

    /**
     * Aggregates veterinarians by their specialization.
     *
     * @return a list of maps containing specialization and veterinarian count.
     */
    @Override
    public List<Map<String, Object>> aggregateVetsBySpecialization() {
        List<Object[]> results = veterinarianRepository.countVetsBySpecialization();
        return results.stream()
                .map(result -> Map.of("specialization", result[0], "count", result[1]))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves available times for booking an appointment with a veterinarian on a specific date.
     *
     * @param vetId the veterinarian's ID.
     * @param date  the date to check availability.
     * @return a list of {@link LocalTime} representing available time slots.
     */
    @Override
    public List<LocalTime> getAvailableTimeForBookAppointment(Long vetId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findByVeterinarianIdAndAppointmentDate(vetId, date);

        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();

        return Stream.iterate(BEGINNING_OF_WORKING_DAY,
                        time -> time.isBefore(END_OF_WORKING_DAY.minusMinutes(UNAVAILABLE_BEFORE_END_OF_WORKING_DAY)),
                        time -> time.plusMinutes(AVAILABLE_PERIOD_FOR_BOOK_APPOINTMENT))
                .filter(time -> {
                    if (date.equals(currentDate)) {
                        return time.isAfter(currentTime.plusHours(MINIMUM_HOURS_FROM_NOW_FOR_APPOINTMENT));
                    }
                    return true;
                })
                .filter(time -> appointments.stream().noneMatch(appointment ->
                        doesAppointmentOverLap(appointment, time, time.plusMinutes(APPOINTMENT_DURATION_MINUTES))
                ))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all veterinarians and converts them to {@link UserDto}.
     *
     * @return a list of {@link UserDto} objects representing all veterinarians.
     */
    @Override
    public List<UserDto> getVeterinarians() {
        List<Veterinarian> veterinarians = veterinarianRepository.findAll();
        return veterinarians.stream()
                .map(v -> entityConverter.mapEntityToDto(v, UserDto.class)).toList();
    }

    /**
     * Retrieves veterinarians by their specialization.
     *
     * @param specialization the specialization to filter veterinarians.
     * @return a list of veterinarians with the specified specialization.
     * @throws ResourceNotFoundException if no veterinarians with the given specialization are found.
     */
    private List<Veterinarian> getVeterinariansBySpecialization(String specialization) {
        if (!veterinarianRepository.existsBySpecialization(specialization)) {
            throw new ResourceNotFoundException(String.format(FeedBackMessage.SPECIALIZATION_NOT_FOUND, specialization));
        }
        return veterinarianRepository.findBySpecializationAndIsEnabled(specialization, true);
    }

    /**
     * Maps a {@link Veterinarian} entity to a {@link UserDto} object, including additional details such as
     * average rating and total reviewers.
     *
     * @param veterinarian the veterinarian entity to map.
     * @return the mapped {@link UserDto} containing veterinarian details.
     */
    private UserDto mapVeterinarianToUserDto(Veterinarian veterinarian, Map<Long, VeterinarianReviewProjection> statsMap) {
        UserDto userDto = entityConverter.mapEntityToDto(veterinarian, UserDto.class);
        VeterinarianReviewProjection stats = statsMap.get(veterinarian.getId());
        if (stats != null) {
            userDto.setAverageRating(stats.getAverageRating() != null ? stats.getAverageRating() : 0.0);
            userDto.setTotalReviewers(stats.getTotalReviewers());
        } else {
            userDto.setAverageRating(0.0);
            userDto.setTotalReviewers(0L);
        }
        return userDto;
    }

    /**
     * Retrieves a list of veterinarians filtered by specialization and availability for a given date and time.
     *
     * @param specialization the specialization to filter veterinarians.
     * @param date           the requested appointment date.
     * @param time           the requested appointment time.
     * @return a list of available veterinarians matching the criteria.
     */
    private List<Veterinarian> getAvailableVeterinarians(String specialization, LocalDate date, LocalTime time) {
        List<Veterinarian> veterinarians = getVeterinariansBySpecialization(specialization);
        return veterinarians.stream()
                .filter(vet -> isVetAvailable(vet, date, time))
                .toList();
    }

    /**
     * Checks if a veterinarian is available for an appointment on a specific date and time.
     *
     * @param veterinarian  the veterinarian to check.
     * @param requestedDate the date of the requested appointment.
     * @param requestedTime the time of the requested appointment.
     * @return {@code true} if the veterinarian is available; {@code false} otherwise.
     */
    private boolean isVetAvailable(Veterinarian veterinarian, LocalDate requestedDate, LocalTime requestedTime) {
        if (requestedDate != null && requestedTime != null) {
            LocalTime requestedEndTime = requestedTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);
            return appointmentRepository.findByVeterinarianAndAppointmentDate(veterinarian, requestedDate)
                    .stream()
                    .noneMatch(existingAppointment -> doesAppointmentOverLap(existingAppointment, requestedTime, requestedEndTime));
        }
        return true;
    }

    /**
     * Determines whether a new appointment time overlaps with an existing appointment's time.
     *
     * @param existingAppointment the existing appointment to compare against.
     * @param requestedStartTime  the start time of the requested appointment.
     * @param requestedEndTime    the end time of the requested appointment.
     * @return {@code true} if the requested appointment overlaps; {@code false} otherwise.
     */
    private boolean doesAppointmentOverLap(Appointment existingAppointment, LocalTime requestedStartTime, LocalTime requestedEndTime) {
        if (existingAppointment.getStatus() == CANCELLED || existingAppointment.getStatus() == NOT_APPROVED) {
            return false;
        }
        LocalTime existingStartTime = existingAppointment.getAppointmentTime();
        LocalTime existingEndTime = existingStartTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);
        LocalTime unavailableStartTime = existingStartTime.minusMinutes(UNAVAILABLE_BEFORE_START_MINUTES);
        LocalTime unavailableEndTime = existingEndTime.plusMinutes(UNAVAILABLE_AFTER_END_MINUTES);
        return !(requestedEndTime.isBefore(unavailableStartTime) || requestedStartTime.isAfter(unavailableEndTime));
    }
}
