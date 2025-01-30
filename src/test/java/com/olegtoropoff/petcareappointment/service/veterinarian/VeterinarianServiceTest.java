package com.olegtoropoff.petcareappointment.service.veterinarian;

import com.olegtoropoff.petcareappointment.dto.EntityConverter;
import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.projection.VeterinarianReviewProjection;
import com.olegtoropoff.petcareappointment.repository.AppointmentRepository;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.service.review.ReviewService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class VeterinarianServiceTest {

    @InjectMocks
    private VeterinarianService veterinarianService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Spy
    private EntityConverter<Veterinarian, UserDto> entityConverter = new EntityConverter<>(new ModelMapper());

    @Test
    void getAllVeterinariansWithRating_ReturnsMappedUserDtos() {
        Long veterinarianId = 1L;
        Veterinarian vet = new Veterinarian();
        vet.setId(veterinarianId);
        List<Veterinarian> veterinarians = List.of(vet);

        Map<Long, VeterinarianReviewProjection> statsMap = Map.of(1L, createMockProjection());

        when(veterinarianRepository.findAllByUserTypeAndIsEnabled("VET", true)).thenReturn(veterinarians);
        when(reviewService.getAverageRatingsAndTotalReviews()).thenReturn(statsMap);
        List<UserDto> result = veterinarianService.getAllVeterinariansWithDetails();

        assertEquals(veterinarians.size(), result.size());
        assertEquals(veterinarians.get(0).getId(), result.get(0).getId());
        assertEquals(4.5, result.get(0).getAverageRating());
        assertEquals(10L, result.get(0).getTotalReviewers());
        verify(entityConverter).mapEntityToDto(vet, UserDto.class);
    }

    private VeterinarianReviewProjection createMockProjection() {
        VeterinarianReviewProjection projection = Mockito.mock(VeterinarianReviewProjection.class);
        Mockito.when(projection.getAverageRating()).thenReturn(4.5);
        Mockito.when(projection.getTotalReviewers()).thenReturn( 10L);
        return projection;
    }

    @Test
    void getSpecializations_Success() {
        List<String> specializations = List.of("Surgery", "Dermatology");

        when(veterinarianRepository.getSpecializations()).thenReturn(specializations);

        List<String> result = veterinarianService.getSpecializations();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(veterinarianRepository).getSpecializations();
    }

    @Test
    void findAvailableVeterinariansForAppointments_Success() {
        String specialization = "Surgery";
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.of(10, 0);

        Veterinarian vet = new Veterinarian();
        vet.setId(1L);
        List<Veterinarian> veterinarians = List.of(vet);

        when(veterinarianRepository.existsBySpecialization(specialization)).thenReturn(true);
        when(veterinarianRepository.findBySpecialization(specialization)).thenReturn(veterinarians);
        when(appointmentRepository.findByVeterinarianAndAppointmentDate(vet, date)).thenReturn(Collections.emptyList());

        List<UserDto> result = veterinarianService.findAvailableVeterinariansForAppointments(specialization, date, time);

        assertNotNull(result);
        assertEquals(result.get(0).getId(), veterinarians.get(0).getId());
        assertEquals(1, result.size());
        verify(entityConverter).mapEntityToDto(vet, UserDto.class);
    }

    @Test
    void getVeterinariansBySpecialization_Success() {
        String specialization = "Surgery";
        Veterinarian vet = new Veterinarian();
        List<Veterinarian> veterinarians = List.of(vet);

        when(veterinarianRepository.existsBySpecialization(specialization)).thenReturn(true);
        when(veterinarianRepository.findBySpecialization(specialization)).thenReturn(veterinarians);

        List<Veterinarian> result = veterinarianService.getVeterinariansBySpecialization(specialization);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(veterinarianRepository).findBySpecialization(specialization);
    }

    @Test
    void getVeterinariansBySpecialization_ThrowsResourceNotFoundException_WhenSpecializationNotFound() {
        String specialization = "Unknown";

        when(veterinarianRepository.existsBySpecialization(specialization)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> veterinarianService.getVeterinariansBySpecialization(specialization));

        assertEquals(String.format(FeedBackMessage.SPECIALIZATION_NOT_FOUND, specialization), exception.getMessage());
    }

    @Test
    void aggregateVetsBySpecialization_Success() {
        Object[] entry = new Object[]{"Surgery", 5L};
        List<Object[]> results = List.<Object[]>of(entry);

        when(veterinarianRepository.countVetsBySpecialization()).thenReturn(results);

        List<Map<String, Object>> result = veterinarianService.aggregateVetsBySpecialization();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Surgery", result.get(0).get("specialization"));
        assertEquals(5L, result.get(0).get("count"));
    }

    @Test
    void getAvailableTimeForBookAppointment_Success() {
        Long vetId = 1L;
        LocalDate date = LocalDate.now().plusDays(1);
        Appointment appointment = new Appointment();
        appointment.setAppointmentTime(LocalTime.of(10, 0));

        when(appointmentRepository.findByVeterinarianIdAndAppointmentDate(vetId, date)).thenReturn(List.of(appointment));

        List<LocalTime> result = veterinarianService.getAvailableTimeForBookAppointment(vetId, date);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
