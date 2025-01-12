package com.olegtoropoff.petcareappointment.scheduler;

import com.olegtoropoff.petcareappointment.service.appointment.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler component for automating the update of appointment statuses.
 * <p>
 * This class runs a scheduled task to periodically check and update the status of
 * appointments in the system. It uses a cron expression to define the schedule.
 */
@Component
@RequiredArgsConstructor
public class AppointmentStatusUpdater {

    /**
     * Service for managing appointments.
     */
    private final IAppointmentService appointmentService;

    /**
     * Cron expression to schedule the task.
     * <p>
     * The cron expression {@code "0 0/5 * 1/1 * ?"} specifies the following:
     * <ul>
     *     <li><strong>Seconds:</strong> {@code 0} - Task runs at the 0th second of the minute.</li>
     *     <li><strong>Minutes:</strong> {@code 0/5} - Task runs every 5 minutes.</li>
     *     <li><strong>Hours:</strong> {@code *} - Task runs every hour.</li>
     *     <li><strong>Day of month:</strong> {@code 1/1} - Task runs every day of the month.</li>
     *     <li><strong>Month:</strong> {@code *} - Task runs every month.</li>
     *     <li><strong>Day of week:</strong> {@code ?} - Task runs on any day of the week.</li>
     * </ul>
     * As a result, this task is executed every 5 minutes.
     */
    private static final String CRON_EXPRESSION = "0 0/5 * 1/1 * ?";

    /**
     * Scheduled task to automate the update of appointment statuses.
     * <p>
     * This method retrieves all appointment IDs and updates their statuses
     * by delegating to the {@link IAppointmentService}.
     * <p>
     * The task runs according to the defined {@link #CRON_EXPRESSION}.
     */
    @Scheduled(cron = CRON_EXPRESSION)
    public void automateAppointmentStatusUpdate(){
        List<Long> appointmentIds = appointmentService.getAppointmentIds();
        for(Long appointmentId : appointmentIds){
            appointmentService.setAppointmentStatus(appointmentId);
        }
    }
}
