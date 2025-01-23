package com.olegtoropoff.petcareappointment.data;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents the default data structure for initializing appointment-related information.
 * This class is used for importing or processing pre-defined appointment data, such as reasons,
 * dates, times, statuses, and associated pets.
 */
@Data
public class DefaultAppointmentData {

    /**
     * A list of {@link AppointmentData} objects representing appointment details.
     */
    private List<AppointmentData> appointments;

    /**
     * Represents the data structure for an individual appointment.
     * <p>
     * This includes information such as the appointment reason, date, time, unique identifier,
     * status, associated patient and veterinarian emails, and the list of associated pets.
     */
    @Data
    public static class AppointmentData {

        /**
         * The reason for the appointment.
         */
        private String reason;

        /**
         * The date of the appointment.
         */
        private LocalDate appointmentDate;

        /**
         * The time of the appointment.
         */
        private LocalTime appointmentTime;

        /**
         * The unique identifier for the appointment.
         */
        private String appointmentNo;

        /**
         * The status of the appointment (e.g., COMPLETED, CANCELLED, NOT_APPROVED).
         */
        private String status;

        /**
         * The email address of the patient associated with the appointment.
         */
        private String patientEmail;

        /**
         * The email address of the veterinarian associated with the appointment.
         */
        private String veterinarianEmail;

        /**
         * A list of {@link PetData} objects representing the pets involved in the appointment.
         */
        private List<PetData> pets;
    }

    /**
     * Represents the data structure for an individual pet associated with an appointment.
     */
    @Data
    public static class PetData {

        /**
         * The name of the pet.
         */
        private String name;

        /**
         * The type of the pet (e.g., dog, cat, bird).
         */
        private String type;

        /**
         * The color of the pet.
         */
        private String color;

        /**
         * The breed of the pet.
         */
        private String breed;

        /**
         * The age of the pet.
         */
        private int age;
    }
}
