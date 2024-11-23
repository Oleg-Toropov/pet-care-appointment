package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    long countByUserType(String type);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isEnabled = :enabled WHERE u.id = :userId")
    void updateUserEnabledStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);

    Optional<User> findByEmail(String email);
}

