package com.olegtoropoff.petcareappointment.dto;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing appointment details.
 * This class encapsulates the information related to an appointment, including
 * its date, time, participants, status, and associated pets.
 */
@Data
public class AppointmentDto {
    /**
     * Unique identifier for the appointment.
     */
    private Long id;

    /**
     * Reason for the appointment.
     */
    private String reason;

    /**
     * Date of the appointment.
     */
    private LocalDate appointmentDate;

    /**
     * Time of the appointment.
     */
    private LocalTime appointmentTime;

    /**
     * Appointment number for identification or tracking.
     */
    private String appointmentNo;

    /**
     * Date when the appointment was created.
     */
    private LocalDate createdAt;

    /**
     * Current status of the appointment.
     * Values are defined in {@link AppointmentStatus}.
     */
    private AppointmentStatus status;

    /**
     * The patient associated with the appointment.
     */
    private UserDto patient;

    /**
     * The veterinarian assigned to the appointment.
     */
    private UserDto veterinarian;

    /**
     * List of pets associated with the appointment.
     */
    private List<PetDto> pets = new ArrayList<>();
}
