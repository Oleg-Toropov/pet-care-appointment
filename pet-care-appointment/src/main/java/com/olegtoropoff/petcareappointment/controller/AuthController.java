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

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.AUTH)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IVerificationTokenService tokenService;
    private final IPasswordResetService passwordResetService;
    private final RabbitMQProducer rabbitMQProducer;

    @PostMapping(UrlMapping.LOGIN)
    public ResponseEntity<CustomApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication =
                    authenticationManager
                            .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
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
