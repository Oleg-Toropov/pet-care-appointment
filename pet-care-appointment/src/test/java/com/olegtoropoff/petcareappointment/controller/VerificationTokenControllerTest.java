package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.VerificationTokenRequest;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@Tag("unit")
public class VerificationTokenControllerTest {

    @InjectMocks
    private VerificationTokenController verificationTokenController;

    @Mock
    private IVerificationTokenService verificationTokenService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    public void validateToken_WhenValidToken_ReturnsValid() {
        String token = "valid-token";
        when(verificationTokenService.validateToken(token)).thenReturn("VALID");

        ResponseEntity<CustomApiResponse> response = verificationTokenController.validateToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.VALID_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).validateToken(token);
    }

    @Test
    public void validateToken_WhenAlreadyVerified_ReturnsVerified() {
        String token = "verified-token";
        when(verificationTokenService.validateToken(token)).thenReturn("VERIFIED");

        ResponseEntity<CustomApiResponse> response = verificationTokenController.validateToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.TOKEN_ALREADY_VERIFIED, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).validateToken(token);
    }

    @Test
    public void validateToken_WhenExpiredToken_ReturnsExpired() {
        String token = "expired-token";
        when(verificationTokenService.validateToken(token)).thenReturn("EXPIRED");

        ResponseEntity<CustomApiResponse> response = verificationTokenController.validateToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.EXPIRED_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).validateToken(token);
    }

    @Test
    public void validateToken_WhenInvalidToken_ReturnsInvalid() {
        String token = "invalid-token";
        when(verificationTokenService.validateToken(token)).thenReturn("INVALID");

        ResponseEntity<CustomApiResponse> response = verificationTokenController.validateToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.INVALID_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).validateToken(token);
    }

    @Test
    public void validateToken_WhenUnknownResult_ReturnsValidationError() {
        String token = "unknown-token";
        when(verificationTokenService.validateToken(token)).thenReturn("UNKNOWN");

        ResponseEntity<CustomApiResponse> response = verificationTokenController.validateToken(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.TOKEN_VALIDATION_ERROR, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).validateToken(token);
    }

    @Test
    public void checkTokenExpiration_WhenTokenNotExpired_ReturnsValid() {
        String token = "valid-token";
        when(verificationTokenService.isTokenExpired(token)).thenReturn(false);

        ResponseEntity<CustomApiResponse> response = verificationTokenController.checkTokenExpiration(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.VALID_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).isTokenExpired(token);
    }

    @Test
    public void checkTokenExpiration_WhenTokenExpired_ReturnsExpired() {
        String token = "expired-token";
        when(verificationTokenService.isTokenExpired(token)).thenReturn(true);

        ResponseEntity<CustomApiResponse> response = verificationTokenController.checkTokenExpiration(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.EXPIRED_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).isTokenExpired(token);
    }

    @Test
    public void saveVerificationTokenForUser_WhenValidRequest_ReturnsSuccess() {
        Long userId = 1L;
        String token = "new-verification-token";

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        VerificationTokenRequest request = new VerificationTokenRequest();
        request.setToken(token);
        request.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(verificationTokenService).saveVerificationTokenForUser(token, user);

        ResponseEntity<CustomApiResponse> response = verificationTokenController.saveVerificationTokenForUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.TOKEN_SAVED_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(userRepository, times(1)).findById(userId);
        verify(verificationTokenService, times(1)).saveVerificationTokenForUser(token, user);
    }

    @Test
    public void saveVerificationTokenForUser_WhenUserNotFound_ReturnsUserNotFound() {
        Long userId = 100L;
        String token = "new-verification-token";

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        VerificationTokenRequest request = new VerificationTokenRequest();
        request.setToken(token);
        request.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> verificationTokenController.saveVerificationTokenForUser(request)
        );

        assertEquals(FeedBackMessage.USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(verificationTokenService, times(0)).saveVerificationTokenForUser(anyString(), any(User.class));
    }

    @Test
    public void generateNewVerificationToken_WhenValidOldToken_ReturnsNewToken() {
        String oldToken = "old-valid-token";
        VerificationToken newToken = new VerificationToken();
        newToken.setToken("new-generated-token");
        newToken.setUser(new User());
        newToken.setExpirationDate(null);

        when(verificationTokenService.generateNewVerificationToken(oldToken)).thenReturn(newToken);

        ResponseEntity<CustomApiResponse> response = verificationTokenController.generateNewVerificationToken(oldToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", Objects.requireNonNull(response.getBody()).getMessage());
        assertNotNull(response.getBody().getData());
        VerificationToken responseToken = (VerificationToken) response.getBody().getData();
        assertEquals("new-generated-token", responseToken.getToken());
        verify(verificationTokenService, times(1)).generateNewVerificationToken(oldToken);
    }

    @Test
    public void deleteUserToken_WhenValidUserId_ReturnsSuccess() {
        Long userId = 5L;
        doNothing().when(verificationTokenService).deleteVerificationToken(userId);

        ResponseEntity<CustomApiResponse> response = verificationTokenController.deleteUserToken(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.TOKEN_DELETE_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(verificationTokenService, times(1)).deleteVerificationToken(userId);
    }
}
