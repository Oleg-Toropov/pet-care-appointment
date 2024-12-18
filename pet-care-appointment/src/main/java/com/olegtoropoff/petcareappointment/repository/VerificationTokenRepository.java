package com.olegtoropoff.petcareappointment.repository;

import com.olegtoropoff.petcareappointment.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> findAllByUserId(Long userId);
}
