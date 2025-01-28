package com.olegtoropoff.petcareappointment.service.token;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.repository.VerificationTokenRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.SystemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for managing verification tokens.
 * This class provides functionality for validating, saving, updating, and deleting
 * verification tokens used during user registration or email verification processes.
 */
@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    /**
     * Validates the given verification token.
     *
     * @param token the token to validate.
     * @return a message indicating the result of the validation process.
     */
    @Override
    public String validateToken(String token) {
        Optional<VerificationToken> theToken = findByToken(token);
        if (theToken.isEmpty()) {
            return FeedBackMessage.INVALID_TOKEN;
        }
        User user = theToken.get().getUser();
        if (user.isEnabled()) {
            return FeedBackMessage.TOKEN_ALREADY_VERIFIED;
        }
        if (isTokenExpired(token)) {
            return FeedBackMessage.EXPIRED_TOKEN;
        }
        user.setEnabled(true);
        userRepository.save(user);
        return FeedBackMessage.VALID_TOKEN;
    }

    /**
     * Saves a new verification token for a user.
     *
     * @param token the token to save.
     * @param user  the associated user.
     */
    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        var verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);
    }

    /**
     * Generates a new verification token based on an existing one.
     *
     * @param oldToken the old token to replace.
     * @return the newly generated verification token.
     * @throws IllegalArgumentException if the old token is invalid.
     */
    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        Optional<VerificationToken> theToken = findByToken(oldToken);

        if (theToken.isPresent()) {
            var verificationToken = theToken.get();
            verificationToken.setToken(UUID.randomUUID().toString());
            verificationToken.setExpirationDate(SystemUtils.getExpirationTime());
            return tokenRepository.save(verificationToken);
        }
        throw new IllegalArgumentException(FeedBackMessage.INVALID_VERIFICATION_TOKEN + oldToken);
    }

    /**
     * Retrieves a verification token by its token value.
     *
     * @param token the token value.
     * @return an Optional containing the verification token if found, or empty otherwise.
     */
    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    /**
     * Checks if a verification token is expired.
     *
     * @param token the token to check.
     * @return true if the token is expired, false otherwise.
     */
    @Override
    public boolean isTokenExpired(String token) {
        Optional<VerificationToken> theToken = findByToken(token);
        if (theToken.isEmpty()) {
            return true;
        }
        VerificationToken verificationToken = theToken.get();
        return verificationToken.getExpirationDate().getTime() <= Calendar.getInstance().getTime().getTime();
    }

    /**
     * Finds the latest verification token associated with a user by their ID.
     *
     * @param userId the ID of the user.
     * @return the latest verification token associated with the user.
     * @throws ResourceNotFoundException if no token is found for the user.
     */
    @Override
    public VerificationToken findTokenByUserId(Long userId) {
        return tokenRepository.findAllByUserId(userId).stream()
                .max(Comparator.comparingLong(VerificationToken::getId))
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
    }
}
