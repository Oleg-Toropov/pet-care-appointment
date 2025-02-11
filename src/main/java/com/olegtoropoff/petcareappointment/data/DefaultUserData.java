package com.olegtoropoff.petcareappointment.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents the default user data used to initialize the application
 * with predefined users, including administrators, veterinarians, and patients.
 * Contains lists of specific user types: admins, veterinarians, and patients.
 * Each type extends the base {@link UserData} class.
 */
@Data
public class DefaultUserData {

    /**
     * List of administrator data.
     */
    private List<AdminData> admins;

    /**
     * List of veterinarian data.
     */
    private List<VeterinarianData> veterinarians;

    /**
     * List of patient data.
     */
    private List<PatientData> patients;

    /**
     * Represents data for an administrator user.
     * Extends {@link UserData} to inherit common user fields.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AdminData extends UserData {
    }

    /**
     * Represents data for a patient user.
     * Extends {@link UserData} to inherit common user fields.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class PatientData extends UserData {
    }

    /**
     * Represents data for a veterinarian user.
     * Extends {@link UserData} to inherit common user fields and adds
     * specific fields like specialization and biography.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class VeterinarianData extends UserData {

        /**
         * The specialization of the veterinarian (e.g., surgeon, dermatologist).
         */
        private String specialization;

        /**
         * The cost of the veterinary appointment.
         * This field represents the price a veterinarian charges for an appointment.
         */
        private BigDecimal appointmentCost;

        /**
         * The address where the veterinarian conducts appointments.
         * This field stores the location of the clinic or private practice.
         */
        private String clinicAddress;

        /**
         * A short biography or description of the veterinarian's professional background.
         */
        private String biography;
    }
}
