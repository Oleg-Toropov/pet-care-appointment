package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.patient.id =:userId OR r.veterinarian.id =:userId ")
    Page<Review> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.patient.id =:userId OR r.veterinarian.id =:userId")
    List<Review> findAllByUserId(@Param("userId") Long userId);

    List<Review> findByVeterinarianId(Long veterinarianId);

    Long countByVeterinarianId(long id);
}
