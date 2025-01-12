package com.olegtoropoff.petcareappointment.service.veterinarian;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.ReviewRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.service.photo.PhotoService;
import com.olegtoropoff.petcareappointment.service.review.ReviewService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final PhotoService photoService;
    private final AppointmentRepository appointmentRepository;
    private final VeterinarianRepository veterinarianRepository;

    /**
     * Retrieves a list of all veterinarians with detailed information.
     *
     * @return a list of {@link UserDto} representing veterinarians with details.
     */
    @Override
    public List<UserDto> getAllVeterinariansWithDetails() {
        List<Veterinarian> veterinarians = veterinarianRepository.findAllByUserType("VET");
        return veterinarians.stream()
                .map(this::mapVeterinarianToUserDto)
                .toList();
    }

    /**
     * Retrieves all available specializations for veterinarians.
     *
     * @return a list of specialization names.
     */
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
        return filteredVets.stream()
                .map(this::mapVeterinarianToUserDto)
                .toList();
    }

    /**
     * Retrieves veterinarians by their specialization.
     *
     * @param specialization the specialization to filter veterinarians.
     * @return a list of veterinarians with the specified specialization.
     * @throws ResourceNotFoundException if no veterinarians with the given specialization are found.
     */
    @Override
    public List<Veterinarian> getVeterinariansBySpecialization(String specialization) {
        if (!veterinarianRepository.existsBySpecialization(specialization)) {
            throw new ResourceNotFoundException(String.format(FeedBackMessage.SPECIALIZATION_NOT_FOUND, specialization));
        }
        return veterinarianRepository.findBySpecialization(specialization);
    }

    /**
     * Maps a {@link Veterinarian} entity to a {@link UserDto} object, including additional details such as
     * average rating, total reviewers, and photo.
     *
     * @param veterinarian the veterinarian entity to map.
     * @return the mapped {@link UserDto} containing veterinarian details.
     */
    private UserDto mapVeterinarianToUserDto(Veterinarian veterinarian) {
        UserDto userDto = entityConverter.mapEntityToDto(veterinarian, UserDto.class);
        double averageRating = reviewService.getAverageRatingForVet(veterinarian.getId());
        Long totalReviewers = reviewRepository.countByVeterinarianId(veterinarian.getId());
        userDto.setAverageRating(averageRating);
        userDto.setTotalReviewers(totalReviewers);
        if (veterinarian.getPhoto() != null) {
            try {
                byte[] photoBytes = photoService.getImageData(veterinarian.getPhoto().getId());
                userDto.setPhoto(photoBytes);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return userDto;
    }

    /**
     * Retrieves a list of veterinarians filtered by specialization and availability for a given date and time.
     *
     * @param specialization the specialization to filter veterinarians.
     * @param date the requested appointment date.
     * @param time the requested appointment time.
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
}
