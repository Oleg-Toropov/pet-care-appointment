package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.repository.VerificationTokenRepository;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.validation.EmailValidator;
import com.olegtoropoff.petcareappointment.validation.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for managing password reset operations.
 * This class provides functionalities to request a password reset,
 * validate a reset token, and reset the user's password.
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService implements IPasswordResetService {
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IVerificationTokenService tokenService;
    private final RabbitMQProducer rabbitMQProducer;

    /**
     * Finds a user associated with the given password reset token and validates the token.
     *
     * @param token    the reset token to be validated
     * @param password the new password to be set
     * @return the {@link User} associated with the token
     * @throws IllegalArgumentException if the token is invalid or if the password does not meet validation criteria
     */
    @Override
    public User findUserByPasswordResetToken(String token, String password) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException(FeedBackMessage.MISSING_PASSWORD);
        }

        if (!PasswordValidator.isValid(password)) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_PASSWORD_FORMAT);
        }

        Optional<User> theUser = tokenRepository.findByToken(token).map(VerificationToken::getUser);
        if (theUser.isEmpty()) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_RESET_TOKEN);
        }

        return theUser.get();
    }

    /**
     * Initiates a password reset request for the user with the specified email.
     * Sends a reset token via RabbitMQ for notification purposes.
     *
     * @param email the email of the user requesting the password reset
     * @throws IllegalArgumentException if the email format is invalid
     * @throws ResourceNotFoundException if no user is found with the specified email
     */
    @Override
    public void requestPasswordReset(String email) {
        if (!EmailValidator.isValid(email)) {
            throw new IllegalArgumentException(FeedBackMessage.INVALID_EMAIL);
        }

        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            String token = UUID.randomUUID().toString();
            tokenService.saveVerificationTokenForUser(token, user);
            rabbitMQProducer.sendMessage("PasswordResetEvent:" + user.getId() + "#" + token);
        }, () -> {
            throw new ResourceNotFoundException(String.format(FeedBackMessage.USER_NOT_FOUND_WITH_EMAIL, email));
        });
    }

    /**
     * Resets the password for the specified user.
     *
     * @param password the new password to be set
     * @param user     the user whose password is to be reset
     * @return a success message if the password is successfully updated
     * @throws IllegalArgumentException if an error occurs during the update process
     */
    @Override
    public String resetPassword(String password, User user) {
        try {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return FeedBackMessage.PASSWORD_RESET_SUCCESS;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
