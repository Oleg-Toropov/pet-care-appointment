package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.request.LoginRequest;
import com.olegtoropoff.petcareappointment.request.PasswordResetRequest;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
import com.olegtoropoff.petcareappointment.response.JwtResponse;
import com.olegtoropoff.petcareappointment.security.jwt.JwtUtils;
import com.olegtoropoff.petcareappointment.security.user.UPCUserDetails;
import com.olegtoropoff.petcareappointment.service.password.IPasswordResetService;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private IVerificationTokenService tokenService;

    @Mock
    private IPasswordResetService passwordResetService;

    @Mock
    private RabbitMQProducer rabbitMQProducer;

    private LoginRequest loginRequest;
    private PasswordResetRequest passwordResetRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@mail.com");
        loginRequest.setPassword("Password123");

        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setToken("reset-token");
        passwordResetRequest.setNewPassword("NewPassword123");
    }

    @Test
    void login_WhenValidRequest_ReturnsJwt() {
        Authentication mockAuth = mock(Authentication.class);

        UPCUserDetails userDetails = new UPCUserDetails(
                1L,
                "test@mail.com",
                "Password123",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_VET"))
        );

        when(mockAuth.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(jwtUtils.generateTokenForUser(mockAuth)).thenReturn("jwt-token-value");

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertEquals(FeedBackMessage.AUTHENTICATION_SUCCESS, response.getBody().getMessage());
        assertInstanceOf(JwtResponse.class, response.getBody().getData());

        JwtResponse jwtResponse = (JwtResponse) response.getBody().getData();
        assertEquals("jwt-token-value", jwtResponse.getToken());
        assertEquals(1L, jwtResponse.getId());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtils, times(1)).generateTokenForUser(mockAuth);
    }


    @Test
    void login_WhenAccountDisabled_Returns401() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new DisabledException("Account disabled"));

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(FeedBackMessage.ACCOUNT_DISABLED, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void login_WhenInvalidPassword_Returns401() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        ResponseEntity<ApiResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Bad credentials", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(FeedBackMessage.INVALID_PASSWORD, response.getBody().getData());
    }

    @Test
    void verifyEmail_WhenValidToken_ReturnsVALID() {
        when(tokenService.validateToken("token-123")).thenReturn("VALID");

        ResponseEntity<ApiResponse> response = authController.verifyEmail("token-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.VALID_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void verifyEmail_WhenAlreadyVerified_ReturnsTOKEN_ALREADY_VERIFIED() {
        when(tokenService.validateToken("token-123")).thenReturn("VERIFIED");

        ResponseEntity<ApiResponse> response = authController.verifyEmail("token-123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.TOKEN_ALREADY_VERIFIED, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void verifyEmail_WhenExpiredToken_Returns410() {
        when(tokenService.validateToken("token-123")).thenReturn("EXPIRED");

        ResponseEntity<ApiResponse> response = authController.verifyEmail("token-123");

        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertEquals(FeedBackMessage.EXPIRED_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void verifyEmail_WhenInvalidToken_Returns410() {
        when(tokenService.validateToken("token-123")).thenReturn("INVALID");

        ResponseEntity<ApiResponse> response = authController.verifyEmail("token-123");

        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertEquals(FeedBackMessage.INVALID_VERIFICATION_TOKEN, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void verifyEmail_WhenUnknownError_Returns500() {
        when(tokenService.validateToken("token-123")).thenReturn("SOME_UNKNOWN_RESULT");

        ResponseEntity<ApiResponse> response = authController.verifyEmail("token-123");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(FeedBackMessage.ERROR, Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void resendVerificationToken_WhenSuccess_ReturnsNEW_VERIFICATION_TOKEN_SENT() {
        VerificationToken mockToken = new VerificationToken();
        User user = new User();
        user.setId(7L);
        mockToken.setUser(user);

        when(tokenService.generateNewVerificationToken("old-token")).thenReturn(mockToken);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        ResponseEntity<ApiResponse> response = authController.resendVerificationToken("old-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.NEW_VERIFICATION_TOKEN_SENT, Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
        verify(tokenService, times(1)).generateNewVerificationToken("old-token");
        verify(rabbitMQProducer, times(1)).sendMessage("RegistrationCompleteEvent:7");
    }

    @Test
    void resendVerificationToken_WhenAnyOtherError_Returns500() {
        doThrow(new RuntimeException("DB error")).when(tokenService).generateNewVerificationToken("old-token");

        ResponseEntity<ApiResponse> response = authController.resendVerificationToken("old-token");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("DB error", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void requestPasswordReset_WhenSuccess_ReturnsPASSWORD_RESET_EMAIL_SENT() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@mail.com");

        doNothing().when(passwordResetService).requestPasswordReset("test@mail.com");

        ResponseEntity<ApiResponse> response = authController.requestPasswordReset(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PASSWORD_RESET_EMAIL_SENT, Objects.requireNonNull(response.getBody()).getMessage());
        verify(passwordResetService, times(1)).requestPasswordReset("test@mail.com");
    }

    @Test
    void requestPasswordReset_WhenResourceNotFoundOrIllegalArg_ReturnsBadRequest() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "test@mail.com");

        doThrow(new ResourceNotFoundException("User not found")).when(passwordResetService).requestPasswordReset("test@mail.com");

        ResponseEntity<ApiResponse> response = authController.requestPasswordReset(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void requestPasswordReset_WhenAnyOtherError_Returns500() {
        Map<String, String> req = new HashMap<>();
        req.put("email", "test@mail.com");

        doThrow(new RuntimeException("DB error")).when(passwordResetService).requestPasswordReset("test@mail.com");

        ResponseEntity<ApiResponse> response = authController.requestPasswordReset(req);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("DB error", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void resetPassword_WhenSuccess_ReturnsOk() {
        User user = new User();
        user.setId(10L);

        when(passwordResetService.findUserByPasswordResetToken("reset-token", "NewPassword123")).thenReturn(user);
        when(passwordResetService.resetPassword("NewPassword123", user)).thenReturn(FeedBackMessage.PASSWORD_RESET_SUCCESS);

        ResponseEntity<ApiResponse> response = authController.resetPassword(passwordResetRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(FeedBackMessage.PASSWORD_RESET_SUCCESS, Objects.requireNonNull(response.getBody()).getMessage());
        verify(passwordResetService, times(1)).findUserByPasswordResetToken("reset-token", "NewPassword123");
        verify(passwordResetService, times(1)).resetPassword("NewPassword123", user);
    }

    @Test
    void resetPassword_WhenIllegalArgument_ReturnsBadRequest() {
        when(passwordResetService.findUserByPasswordResetToken("reset-token", "NewPassword123"))
                .thenThrow(new IllegalArgumentException("Token invalid"));

        ResponseEntity<ApiResponse> response = authController.resetPassword(passwordResetRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token invalid", Objects.requireNonNull(response.getBody()).getMessage());
        assertNull(response.getBody().getData());
    }
}
