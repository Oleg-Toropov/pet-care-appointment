package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
