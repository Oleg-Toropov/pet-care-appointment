package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.response.CustomApiResponse;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class VerificationTokenControllerTest {

    @InjectMocks
    private VerificationTokenController verificationTokenController;

    @Mock
    private IVerificationTokenService verificationTokenService;

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
}
