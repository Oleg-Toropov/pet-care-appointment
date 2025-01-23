package com.olegtoropoff.petcareappointment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a veterinary appointment that includes details such as reason, date, time,
 * associated patient and veterinarian, and the list of pets involved in the appointment.
 * <p>
 * This class serves as the central entity for scheduling and managing appointments between patients and veterinarians.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "patient", "veterinarian"})
public class Appointment {

    /**
     * Unique identifier for the appointment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The reason or purpose for the appointment (e.g., "Routine checkup").
     */
    private String reason;

    /**
     * The date on which the appointment is scheduled.
     */
    private LocalDate appointmentDate;

    /**
     * The time at which the appointment is scheduled.
     */
    private LocalTime appointmentTime;

    /**
     * A unique number assigned to the appointment for reference.
     */
    private String appointmentNo;

    /**
     * The date when the appointment was created.
     * <p>
     * Automatically populated using {@link CreationTimestamp}.
     */
    @CreationTimestamp
    private LocalDate createdAt;

    /**
     * The current status of the appointment.
     * <p>
     * Possible values are defined in {@link AppointmentStatus}.
     */
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    /**
     * The patient associated with the appointment.
     * <p>
     * Represents the user who booked the appointment.
     */
    @JoinColumn(name = "sender")
    @ManyToOne(fetch = FetchType.LAZY)
    private User patient;

    /**
     * The veterinarian assigned to the appointment.
     */
    @JoinColumn(name = "recipient")
    @ManyToOne(fetch = FetchType.LAZY)
    private User veterinarian;

    /**
     * List of pets associated with the appointment.
     * <p>
     * Each pet is linked to the appointment, capturing details about the animals involved.
     */
    @OneToMany(mappedBy = "appointment", cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<Pet> pets = new ArrayList<>();

    /**
     * Associates a patient with this appointment.
     * <p>
     * Adds the appointment to the patient's list of appointments.
     *
     * @param sender the user who booked the appointment.
     */
    public void addPatient(User sender) {
        this.setPatient(sender);
        if (sender.getAppointments() == null) {
            sender.setAppointments(new ArrayList<>());
        }
        sender.getAppointments().add(this);
    }

    /**
     * Associates a veterinarian with this appointment.
     * <p>
     * Adds the appointment to the veterinarian's list of appointments.
     *
     * @param recipient the veterinarian assigned to the appointment.
     */
    public void addVeterinarian(User recipient) {
        this.setVeterinarian(recipient);
        if (recipient.getAppointments() == null) {
            recipient.setAppointments(new ArrayList<>());
        }
        recipient.getAppointments().add(this);
    }

    /**
     * Generates and assigns a unique appointment number.
     */
    public void setAppointmentNo() {
        this.appointmentNo = String.valueOf(new Random().nextLong()).substring(1, 11);
    }
}
