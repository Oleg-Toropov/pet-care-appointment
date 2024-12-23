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

@Service
@RequiredArgsConstructor
public class PasswordResetService implements IPasswordResetService {
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IVerificationTokenService tokenService;
    private final RabbitMQProducer rabbitMQProducer;

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
