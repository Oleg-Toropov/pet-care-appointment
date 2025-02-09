package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.request.LoginRequest;
import com.olegtoropoff.petcareappointment.request.PasswordResetRequest;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.response.JwtResponse;
import com.olegtoropoff.petcareappointment.security.jwt.JwtUtils;
import com.olegtoropoff.petcareappointment.security.user.UPCUserDetails;
import com.olegtoropoff.petcareappointment.service.password.IPasswordResetService;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * REST controller for managing authentication and authorization operations.
 * Handles user login, email verification, password reset, and token-related actions.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.AUTH)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IVerificationTokenService tokenService;
    private final IPasswordResetService passwordResetService;
    private final RabbitMQProducer rabbitMQProducer;

    /**
     * Authenticates the user and returns a JWT token upon successful login.
     *
     * @param request the login request containing email and password.
     * @return a JWT token if authentication succeeds or an error message if it fails.
     */
    @PostMapping(UrlMapping.LOGIN)
    public ResponseEntity<CustomApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication =
                    authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail().trim(), request.getPassword().trim()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            UPCUserDetails userDetails = (UPCUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.AUTHENTICATION_SUCCESS, jwtResponse));
        } catch (DisabledException e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new CustomApiResponse(FeedBackMessage.ACCOUNT_DISABLED, null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED)
                    .body(new CustomApiResponse(e.getMessage(), FeedBackMessage.INVALID_PASSWORD));
        }
    }

    /**
     * Verifies the user's email using the provided verification token.
     *
     * @param token the verification token.
     * @return a success or error message based on the verification result.
     */
    @GetMapping(UrlMapping.VERIFY_EMAIL)
    public ResponseEntity<CustomApiResponse> verifyEmail(@RequestParam("token") String token) {
        String result = tokenService.validateToken(token);
        return switch (result) {
            case "VALID" -> ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.VALID_TOKEN, null));
            case "VERIFIED" -> ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.TOKEN_ALREADY_VERIFIED, null));
            case "EXPIRED" ->
                    ResponseEntity.status(HttpStatus.GONE).body(new CustomApiResponse(FeedBackMessage.EXPIRED_TOKEN, null));
            case "INVALID" ->
                    ResponseEntity.status(HttpStatus.GONE).body(new CustomApiResponse(FeedBackMessage.INVALID_VERIFICATION_TOKEN, null));
            default -> ResponseEntity.internalServerError().body(new CustomApiResponse(FeedBackMessage.ERROR, null));
        };
    }

    /**
     * Resends a new verification token to the user.
     *
     * @param oldToken the old expired or invalid token.
     * @return a success or error message based on the token regeneration process.
     */
    @PutMapping(UrlMapping.RESEND_VERIFICATION_TOKEN)
    public ResponseEntity<CustomApiResponse> resendVerificationToken(@RequestParam("token") String oldToken) {
        try {
            VerificationToken verificationToken = tokenService.generateNewVerificationToken(oldToken);
            rabbitMQProducer.sendMessage("RegistrationCompleteEvent:" + verificationToken.getUser().getId());
            return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.NEW_VERIFICATION_TOKEN_SENT, null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomApiResponse(e.getMessage(), null));
        }
    }

    /**
     * Initiates the password reset process by sending a reset email.
     *
     * @param requestBody the request containing the user's email.
     * @return a success or error message indicating the status of the reset request.
     */
    @PostMapping(UrlMapping.REQUEST_PASSWORD_RESET)
    public ResponseEntity<CustomApiResponse> requestPasswordReset(@RequestBody Map<String, String> requestBody) {
        try {
            passwordResetService.requestPasswordReset(requestBody.get("email"));
            return ResponseEntity.
                    ok(new CustomApiResponse(FeedBackMessage.PASSWORD_RESET_EMAIL_SENT, null));
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new CustomApiResponse(e.getMessage(), null));
        }
    }

    /**
     * Resets the user's password using a valid reset token.
     *
     * @param request the password reset request containing the token and new password.
     * @return a success or error message based on the reset process.
     */
    @PostMapping(UrlMapping.RESET_PASSWORD)
    public ResponseEntity<CustomApiResponse> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            User user = passwordResetService.findUserByPasswordResetToken(request.getToken(), request.getNewPassword());
            String message = passwordResetService.resetPassword(request.getNewPassword(), user);
            return ResponseEntity.ok(new CustomApiResponse(message, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new CustomApiResponse(e.getMessage(), null));
        }
    }
}
