package com.olegtoropoff.petcareappointment.service.veterinarian;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.model.Veterinarian;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface IVeterinarianService {
    List<UserDto> getAllVeterinariansWithDetails();

    List<String> getSpecializations();

    List<UserDto> findAvailableVeterinariansForAppointments(String specialization, LocalDate date, LocalTime time);

    List<Veterinarian> getVeterinariansBySpecialization(String specialization);

    List<Map<String, Object>> aggregateVetsBySpecialization();

    List<LocalTime> getAvailableTimeForBookAppointment(Long vetId, LocalDate date);
}
