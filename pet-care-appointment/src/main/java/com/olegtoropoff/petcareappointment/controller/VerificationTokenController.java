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

@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.TOKEN_VERIFICATION)
public class VerificationTokenController {
    private final IVerificationTokenService verificationTokenService;
    private final UserRepository userRepository;

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

    @PostMapping(UrlMapping.SAVE_TOKEN)
    public ResponseEntity<CustomApiResponse> saveVerificationTokenForUser(@RequestBody VerificationTokenRequest request) {
        User user = userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new RuntimeException(FeedBackMessage.USER_NOT_FOUND));
        verificationTokenService.saveVerificationTokenForUser(request.getToken(), user);
        return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.TOKEN_SAVED_SUCCESS, null));
    }

    @PutMapping(UrlMapping.GENERATE_NEW_TOKEN_FOR_USER)
    public ResponseEntity<CustomApiResponse> generateNewVerificationToken(@RequestParam String oldToken) {
        VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);
        return ResponseEntity.ok(new CustomApiResponse("", newToken));
    }

    @DeleteMapping(UrlMapping.DELETE_TOKEN)
    public ResponseEntity<CustomApiResponse> deleteUserToken(@RequestParam Long userId) {
        verificationTokenService.deleteVerificationToken(userId);
        return ResponseEntity.ok(new CustomApiResponse(FeedBackMessage.TOKEN_DELETE_SUCCESS, null));
    }
}
