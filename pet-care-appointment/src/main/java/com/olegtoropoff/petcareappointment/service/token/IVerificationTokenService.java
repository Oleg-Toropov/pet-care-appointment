package com.olegtoropoff.petcareappointment.service.token;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;

import java.util.Optional;

public interface IVerificationTokenService {
    String validateToken(String token);
    void saveVerificationTokenForUser(String token, User user);
    VerificationToken generateNewVerificationToken(String oldToken);
    Optional<VerificationToken> findByToken(String token);
    void deleteVerificationToken(Long tokenId);
    boolean isTokenExpired(String token);
}
