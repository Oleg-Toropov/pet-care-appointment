package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing verification tokens. Provides endpoints for token validation,
 * expiration checks, creation, regeneration, and deletion.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.TOKEN_VERIFICATION)
public class VerificationTokenController {
    private final IVerificationTokenService verificationTokenService;

    /**
     * Checks whether a given token is expired.
     *
     * @param token the verification token to check
     * @return a {@link ResponseEntity} indicating whether the token is expired
     */
    @GetMapping(UrlMapping.CHECK_TOKEN_EXPIRATION)
    public ResponseEntity<CustomApiResponse> checkTokenExpiration(String token) {
        boolean isExpired = verificationTokenService.isTokenExpired(token);
        CustomApiResponse response;
        if (isExpired) {
            response = new CustomApiResponse(FeedBackMessage.EXPIRED_TOKEN, null);
        } else {
            response = new CustomApiResponse(FeedBackMessage.VALID_TOKEN, null);
        }
        return ResponseEntity.ok(response);
    }
}
