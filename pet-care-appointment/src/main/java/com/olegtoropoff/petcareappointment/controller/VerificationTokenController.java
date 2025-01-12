package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.VerificationTokenRequest;
import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing verification tokens. Provides endpoints for token validation,
 * expiration checks, creation, regeneration, and deletion.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.TOKEN_VERIFICATION)
public class VerificationTokenController {
    private final IVerificationTokenService verificationTokenService;
    private final UserRepository userRepository;

    /**
     * Validates a given verification token.
     *
     * @param token the verification token to validate
     * @return a {@link ResponseEntity} with a validation status message
     */
    @GetMapping(UrlMapping.VALIDATE_TOKEN)
    public ResponseEntity<CustomApiResponse> validateToken(String token) {
        String result = verificationTokenService.validateToken(token);
        CustomApiResponse response = switch (result) {
            case "INVALID" -> new CustomApiResponse(FeedBackMessage.INVALID_TOKEN, null);
            case "VERIFIED" -> new CustomApiResponse(FeedBackMessage.TOKEN_ALREADY_VERIFIED, null);
            case "EXPIRED" -> new CustomApiResponse(FeedBackMessage.EXPIRED_TOKEN, null);
            case "VALID" -> new CustomApiResponse(FeedBackMessage.VALID_TOKEN, null);
            default -> new CustomApiResponse(FeedBackMessage.TOKEN_VALIDATION_ERROR, null);
        };
        return ResponseEntity.ok(response);
    }

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

    /**
     * Saves a verification token for a user.
     *
     * @param request the request containing user and token information
     * @return a {@link ResponseEntity} indicating the success of the operation
     */
    @PostMapping(UrlMapping.SAVE_TOKEN)
    public ResponseEntity<CustomApiResponse> saveVerificationTokenForUser(@RequestBody VerificationTokenRequest request) {
        User user = userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new RuntimeException(FeedBackMessage.USER_NOT_FOUND));
        verificationTokenService.saveVerificationTokenForUser(request.getToken(), user);
        return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.TOKEN_SAVED_SUCCESS, null));
    }

    /**
     * Generates a new verification token for a user using an old token.
     *
     * @param oldToken the old token to regenerate a new one
     * @return a {@link ResponseEntity} containing the new token details
     */
    @PutMapping(UrlMapping.GENERATE_NEW_TOKEN_FOR_USER)
    public ResponseEntity<CustomApiResponse> generateNewVerificationToken(@RequestParam String oldToken) {
        VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);
        return ResponseEntity.ok(new CustomApiResponse("", newToken));
    }

    /**
     * Deletes a verification token associated with a user ID.
     *
     * @param userId the ID of the user whose token will be deleted
     * @return a {@link ResponseEntity} indicating the success of the operation
     */
    @DeleteMapping(UrlMapping.DELETE_TOKEN)
    public ResponseEntity<CustomApiResponse> deleteUserToken(@RequestParam Long userId) {
        verificationTokenService.deleteVerificationToken(userId);
        return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.TOKEN_DELETE_SUCCESS, null));
    }
}
