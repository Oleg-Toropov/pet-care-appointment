package com.olegtoropoff.petcareappointment.dto;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class AppointmentDto {
    private Long id;
    private String reason;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String appointmentNo;
    private LocalDate createdAt;
    private AppointmentStatus status;
    private UserDto patient;
    private UserDto veterinarian;
    List<PetDto> pets = new ArrayList<>();
}
