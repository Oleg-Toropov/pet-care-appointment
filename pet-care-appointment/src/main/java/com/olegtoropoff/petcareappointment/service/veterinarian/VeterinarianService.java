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

@Service
@RequiredArgsConstructor
public class VeterinarianService implements IVeterinarianService {
    public static final int APPOINTMENT_DURATION_MINUTES = 60;
    private static final int UNAVAILABLE_BEFORE_START_MINUTES = 10;
    private static final int UNAVAILABLE_AFTER_END_MINUTES = 10;
    private static final LocalTime BEGINNING_OF_WORKING_DAY = LocalTime.of(9, 0);
    private static final LocalTime END_OF_WORKING_DAY = LocalTime.of(21, 0);
    private static final int UNAVAILABLE_BEFORE_END_OF_WORKING_DAY = 70;
    private static final int AVAILABLE_PERIOD_FOR_BOOK_APPOINTMENT = 30;
    private static final int MINIMUM_HOURS_FROM_NOW_FOR_APPOINTMENT = 2;

    private final EntityConverter<Veterinarian, UserDto> entityConverter;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final PhotoService photoService;
    private final AppointmentRepository appointmentRepository;
    private final VeterinarianRepository veterinarianRepository;

    @Override
    public List<UserDto> getAllVeterinariansWithDetails() {
        List<Veterinarian> veterinarians = veterinarianRepository.findAllByUserType("VET");
        return veterinarians.stream()
                .map(this::mapVeterinarianToUserDto)
                .toList();
    }

    @Override
    public List<String> getSpecializations() {
        return veterinarianRepository.getSpecializations();
    }

    @Override
    public List<UserDto> findAvailableVeterinariansForAppointments(String specialization, LocalDate date, LocalTime time) {
        List<Veterinarian> filteredVets = getAvailableVeterinarians(specialization, date, time);
        return filteredVets.stream()
                .map(this::mapVeterinarianToUserDto)
                .toList();
    }

    @Override
    public List<Veterinarian> getVeterinariansBySpecialization(String specialization) {
        if (!veterinarianRepository.existsBySpecialization(specialization)) {
            throw new ResourceNotFoundException(String.format(FeedBackMessage.SPECIALIZATION_NOT_FOUND, specialization));
        }
        return veterinarianRepository.findBySpecialization(specialization);
    }

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

    private List<Veterinarian> getAvailableVeterinarians(String specialization, LocalDate date, LocalTime time) {
        List<Veterinarian> veterinarians = getVeterinariansBySpecialization(specialization);
        return veterinarians.stream()
                .filter(vet -> isVetAvailable(vet, date, time))
                .toList();
    }

    private boolean isVetAvailable(Veterinarian veterinarian, LocalDate requestedDate, LocalTime requestedTime) {
        if (requestedDate != null && requestedTime != null) {
            LocalTime requestedEndTime = requestedTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);
            return appointmentRepository.findByVeterinarianAndAppointmentDate(veterinarian, requestedDate)
                    .stream()
                    .noneMatch(existingAppointment -> doesAppointmentOverLap(existingAppointment, requestedTime, requestedEndTime));
        }
        return true;
    }

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

    @Override
    public List<Map<String, Object>> aggregateVetsBySpecialization() {
        List<Object[]> results = veterinarianRepository.countVetsBySpecialization();
        return results.stream()
                .map(result -> Map.of("specialization", result[0], "count", result[1]))
                .collect(Collectors.toList());
    }

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
