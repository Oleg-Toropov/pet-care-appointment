package com.olegtoropoff.petcareappointment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for updating appointment details.
 * <p>
 * This class is used to encapsulate the data needed for updating an existing appointment,
 * such as the date, time, and reason for the appointment.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUpdateRequest {

    /**
     * The updated appointment date in string format (e.g., "2024-12-27").
     */
    private String appointmentDate;

    /**
     * The updated appointment time in string format (e.g., "09:30").
     */
    private String appointmentTime;

    /**
     * The updated reason for the appointment (e.g., "Routine checkup").
     */
    private String reason;
}
