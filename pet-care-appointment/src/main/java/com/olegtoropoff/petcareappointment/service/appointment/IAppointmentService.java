package com.olegtoropoff.petcareappointment.service.appointment;

import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;

import java.util.List;

public interface IAppointmentService {
    Appointment createAppointment(Appointment appointment, Long sender, Long recipient);
    Appointment updateAppointment(Long id, AppointmentUpdateRequest request);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    Appointment getAppointmentByNo(String appointmentNo);
    void deleteAppointment(Long id);
}
