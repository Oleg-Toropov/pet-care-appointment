package com.olegtoropoff.petcareappointment.service.appointment;

import com.olegtoropoff.petcareappointment.dto.AppointmentDto;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import com.olegtoropoff.petcareappointment.request.AppointmentUpdateRequest;
import com.olegtoropoff.petcareappointment.request.BookAppointmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IAppointmentService {
    Appointment createAppointment(BookAppointmentRequest request, Long sender, Long recipient);

    Appointment updateAppointment(Long id, AppointmentUpdateRequest request);

    Appointment addPetForAppointment(Long id, Pet pet);

    Page<AppointmentDto> getAllAppointments(Pageable pageable);

    Appointment getAppointmentById(Long id);

    Appointment getAppointmentByNo(String appointmentNo);

    void deleteAppointment(Long id);

    List<AppointmentDto> getUserAppointments(Long userId);

    Appointment cancelAppointment(Long appointmentId);

    Appointment approveAppointment(Long appointmentId);

    Appointment declineAppointment(Long appointmentId);

    long countAppointments();

    List<Map<String, Object>> getAppointmentSummary();

    List<Long> getAppointmentIds();

    void setAppointmentStatus(Long appointmentId);
}
