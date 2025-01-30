package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.repository.VerificationTokenRepository;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IVerificationTokenService tokenService;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    @Test
    void findUserByPasswordResetToken_Success() {
        String token = "valid-token";
        String password = "ValidPassword1!";

        User user = new User();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        User result = passwordResetService.findUserByPasswordResetToken(token, password);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void findUserByPasswordResetToken_ThrowsException_WhenTokenIsInvalid() {
        String token = "invalid-token";
        String password = "ValidPassword1!";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.findUserByPasswordResetToken(token, password));

        assertEquals(FeedBackMessage.INVALID_RESET_TOKEN, exception.getMessage());
    }

    @Test
    void requestPasswordReset_Success() {
        String email = "test@gmail.com";

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(tokenService).saveVerificationTokenForUser(anyString(), eq(user));
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        passwordResetService.requestPasswordReset(email);

        verify(tokenService, times(1)).saveVerificationTokenForUser(anyString(), eq(user));
        verify(rabbitMQProducer, times(1)).sendMessage(anyString());
    }

    @Test
    void requestPasswordReset_ThrowsException_WhenEmailIsInvalid() {
        String email = "invalid-email";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.requestPasswordReset(email));

        assertEquals(FeedBackMessage.INVALID_EMAIL, exception.getMessage());
    }

    @Test
    void requestPasswordReset_ThrowsException_WhenUserNotFound() {
        String email = "notfound@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> passwordResetService.requestPasswordReset(email));

        assertEquals(String.format(FeedBackMessage.USER_NOT_FOUND_WITH_EMAIL, email), exception.getMessage());
    }

    @Test
    void resetPassword_Success() {
        String password = "ValidPassword1!";
        User user = new User();

        when(passwordEncoder.encode(password)).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        String result = passwordResetService.resetPassword(password, user);

        assertEquals(FeedBackMessage.PASSWORD_RESET_SUCCESS, result);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void resetPassword_ThrowsException_WhenErrorOccurs() {
        String password = "ValidPassword1!";
        User user = new User();

        when(passwordEncoder.encode(password)).thenThrow(new RuntimeException("Encoding error"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(password, user));

        assertEquals("Encoding error", exception.getMessage());
    }
}
