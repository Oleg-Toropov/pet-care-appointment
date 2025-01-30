package com.olegtoropoff.petcareappointment.service.token;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.repository.VerificationTokenRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class VerificationTokenServiceTest {

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Test
    void validateToken_ValidToken() {
        String token = "validToken";
        User user = new User();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() + 60000));

        when(tokenRepository.findByToken(token))
                .thenReturn(Optional.of(verificationToken))
                .thenReturn(Optional.of(verificationToken));
        when(userRepository.save(user)).thenReturn(user);

        String result = verificationTokenService.validateToken(token);

        assertEquals(FeedBackMessage.VALID_TOKEN, result);
        assertTrue(user.isEnabled());
        verify(tokenRepository, times(2)).findByToken(token);
        verify(userRepository).save(user);
    }

    @Test
    void validateToken_ExpiredToken() {
        String expiredToken = "expiredToken";
        User mockUser = new User();
        mockUser.setEnabled(false);
        VerificationToken mockToken = new VerificationToken();
        mockToken.setUser(mockUser);
        mockToken.setExpirationDate(new Date(System.currentTimeMillis() - 60000));

        when(tokenRepository.findByToken(expiredToken))
                .thenReturn(Optional.of(mockToken))
                .thenReturn(Optional.of(mockToken));

        String result = verificationTokenService.validateToken(expiredToken);

        assertEquals(FeedBackMessage.EXPIRED_TOKEN, result);

        verify(tokenRepository, times(2)).findByToken(expiredToken);
    }



    @Test
    void validateToken_InvalidToken() {
        String token = "invalidToken";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        String result = verificationTokenService.validateToken(token);

        assertEquals(FeedBackMessage.INVALID_TOKEN, result);
        verify(tokenRepository).findByToken(token);
        verifyNoInteractions(userRepository);
    }

    @Test
    void saveVerificationTokenForUser_Success() {
        String token = UUID.randomUUID().toString();
        User user = new User();
        VerificationToken verificationToken = new VerificationToken(token, user);

        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

        verificationTokenService.saveVerificationTokenForUser(token, user);

        verify(tokenRepository).save(any(VerificationToken.class));
    }

    @Test
    void generateNewVerificationToken_Success() {
        String oldToken = "oldToken";
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(oldToken);

        when(tokenRepository.findByToken(oldToken)).thenReturn(Optional.of(verificationToken));
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

        VerificationToken result = verificationTokenService.generateNewVerificationToken(oldToken);

        assertNotNull(result);
        assertNotEquals(oldToken, result.getToken());
        verify(tokenRepository).findByToken(oldToken);
        verify(tokenRepository).save(verificationToken);
    }

    @Test
    void generateNewVerificationToken_InvalidOldToken() {
        String oldToken = "nonExistentToken";

        when(tokenRepository.findByToken(oldToken)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> verificationTokenService.generateNewVerificationToken(oldToken));

        assertEquals(FeedBackMessage.INVALID_VERIFICATION_TOKEN + oldToken, exception.getMessage());
        verify(tokenRepository).findByToken(oldToken);
    }

    @Test
    void isTokenExpired_True() {
        String token = "expiredToken";
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() - 60000));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        boolean result = verificationTokenService.isTokenExpired(token);

        assertTrue(result);
        verify(tokenRepository).findByToken(token);
    }

    @Test
    void isTokenExpired_False() {
        String token = "validToken";
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() + 60000));

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        boolean result = verificationTokenService.isTokenExpired(token);

        assertFalse(result);
        verify(tokenRepository).findByToken(token);
    }

    @Test
    void findTokenByUserId_Success() {
        Long userId = 1L;
        VerificationToken token1 = new VerificationToken();
        token1.setId(1L);
        VerificationToken token2 = new VerificationToken();
        token2.setId(2L);

        when(tokenRepository.findAllByUserId(userId)).thenReturn(List.of(token1, token2));

        VerificationToken result = verificationTokenService.findTokenByUserId(userId);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(tokenRepository).findAllByUserId(userId);
    }

    @Test
    void findTokenByUserId_ThrowsException_WhenNoTokens() {
        Long userId = 1L;

        when(tokenRepository.findAllByUserId(userId)).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> verificationTokenService.findTokenByUserId(userId));

        assertEquals(FeedBackMessage.RESOURCE_NOT_FOUND, exception.getMessage());
        verify(tokenRepository).findAllByUserId(userId);
    }
}
