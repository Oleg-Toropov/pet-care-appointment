package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.repository.UserRepository;
import com.olegtoropoff.petcareappointment.request.VerificationTokenRequest;
import com.olegtoropoff.petcareappointment.response.ApiResponse;
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
    public ResponseEntity<ApiResponse> validateToken(String token) {
        String result = verificationTokenService.validateToken(token);
        ApiResponse response = switch (result) {
            case "INVALID" -> new ApiResponse(FeedBackMessage.INVALID_TOKEN, null);
            case "VERIFIED" -> new ApiResponse(FeedBackMessage.TOKEN_ALREADY_VERIFIED, null);
            case "EXPIRED" -> new ApiResponse(FeedBackMessage.EXPIRED_TOKEN, null);
            case "VALID" -> new ApiResponse(FeedBackMessage.VALID_TOKEN, null);
            default -> new ApiResponse(FeedBackMessage.TOKEN_VALIDATION_ERROR, null);
        };
        return ResponseEntity.ok(response);
    }

    @GetMapping(UrlMapping.CHECK_TOKEN_EXPIRATION)
    public ResponseEntity<ApiResponse> checkTokenExpiration(String token) {
        boolean isExpired = verificationTokenService.isTokenExpired(token);
        ApiResponse response;
        if (isExpired) {
            response = new ApiResponse(FeedBackMessage.EXPIRED_TOKEN, null);
        } else {
            response = new ApiResponse(FeedBackMessage.VALID_TOKEN, null);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(UrlMapping.SAVE_TOKEN)
    public ResponseEntity<ApiResponse> saveVerificationTokenForUser(@RequestBody VerificationTokenRequest request) {
        User user = userRepository.findById(request.getUser().getId())
                .orElseThrow(() -> new RuntimeException(FeedBackMessage.USER_NOT_FOUND));
        verificationTokenService.saveVerificationTokenForUser(request.getToken(), user);
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.TOKEN_SAVED_SUCCESS, null));
    }

    @PutMapping(UrlMapping.GENERATE_NEW_TOKEN_FOR_USER)
    public ResponseEntity<ApiResponse> generateNewVerificationToken(@RequestParam String oldToken) {
        VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);
        return ResponseEntity.ok(new ApiResponse("", newToken));
    }

    @DeleteMapping(UrlMapping.DELETE_TOKEN)
    public ResponseEntity<ApiResponse> deleteUserToken(@RequestParam Long userId) {
        verificationTokenService.deleteVerificationToken(userId);
        return ResponseEntity.ok(new ApiResponse(FeedBackMessage.TOKEN_DELETE_SUCCESS, null));
    }
}
