package com.olegtoropoff.petcareappointment.enums;

/**
 * Represents the various statuses an appointment can have throughout its lifecycle.
 * <p>
 * This enum defines the different states that an appointment may transition through,
 * such as being scheduled, approved, or completed.
 */
public enum AppointmentStatus {

    /**
     * Indicates that the appointment has been canceled.
     */
    CANCELLED,

    /**
     * Indicates that the appointment is currently in progress.
     */
    ON_GOING,

    /**
     * Indicates that the appointment is scheduled for a future date and time.
     */
    UP_COMING,

    /**
     * Indicates that the appointment has been approved by the relevant party.
     */
    APPROVED,

    /**
     * Indicates that the appointment was not approved.
     */
    NOT_APPROVED,

    /**
     * Indicates that the appointment is waiting for approval from the relevant party.
     */
    WAITING_FOR_APPROVAL,

    /**
     * Indicates that the appointment has been completed.
     */
    COMPLETED
}
