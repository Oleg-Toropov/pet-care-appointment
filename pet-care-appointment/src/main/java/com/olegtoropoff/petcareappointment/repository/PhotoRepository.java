package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
