package com.olegtoropoff.petcareappointment.request;

import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request object for booking a new appointment.
 * <p>
 * This class is used to encapsulate the details required for booking an appointment,
 * including the appointment itself and the associated pets.
 */
@Getter
@Setter
public class BookAppointmentRequest {

    /**
     * The details of the appointment being booked.
     * <p>
     * This includes information such as the date, time, reason, and status of the appointment.
     */
    private Appointment appointment;

    /**
     * A list of pets associated with the appointment.
     * <p>
     * Each pet object contains details such as name, type, breed, and age.
     */
    private List<Pet> pets;
}
