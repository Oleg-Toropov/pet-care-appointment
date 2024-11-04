package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {
    List<Veterinarian> findBySpecialization(String specialization);

    boolean existsBySpecialization(String specialization);

    @Query("SELECT DISTINCT v.specialization FROM Veterinarian v")
    List<String> getSpecializations();

    @Query("SELECT v.specialization AS specialization, COUNT(v) AS count FROM Veterinarian  v GROUP BY v.specialization")
    List<Object[]> countVetsBySpecialization();
}
