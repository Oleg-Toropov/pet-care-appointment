package com.olegtoropoff.petcareappointment.service.appointment;

import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;

import java.util.List;

public interface IAppointmentService {
    Appointment createAppointment(BookAppointmentRequest request, Long sender, Long recipient);

    Appointment updateAppointment(Long id, AppointmentUpdateRequest request);

    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(Long id);

    Appointment getAppointmentByNo(String appointmentNo);

    void deleteAppointment(Long id);
}
