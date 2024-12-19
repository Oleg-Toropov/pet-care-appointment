package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.enums.AppointmentStatus;
import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.patient.id =:userId OR a.veterinarian.id =:userId")
    List<Appointment> findAllByUserId(@Param("userId") Long userId);

    List<Appointment> findByVeterinarianAndAppointmentDate(User veterinarian, LocalDate requestedDate);

    List<Appointment> findByVeterinarianIdAndAppointmentDate(Long veterinarianId, LocalDate requestedDate);

    @NonNull
    Page<Appointment> findAll(@NonNull Pageable pageable);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient.id = :senderId AND a.status NOT IN :excludedStatuses")
    int countByPatientIdAndStatusNotIn(@Param("senderId") Long senderId, @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses);

    boolean existsByVeterinarianIdAndPatientIdAndStatus(Long veterinarianId, Long reviewerId, AppointmentStatus appointmentStatus);
}
