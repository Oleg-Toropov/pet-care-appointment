package com.olegtoropoff.petcareappointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.repository.VerificationTokenRepository;
import com.olegtoropoff.petcareappointment.request.VerificationTokenRequest;
import com.olegtoropoff.petcareappointment.service.token.IVerificationTokenService;
import com.olegtoropoff.petcareappointment.service.user.UserService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.JwtTestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_pet_care_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class VerificationTokenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTestUtils jwtTestUtils;

    @Autowired
    private IVerificationTokenService tokenService;

    @Test
    @Transactional
    void validateToken_WhenValidToken_ReturnsVALID() throws Exception {
        User user = userService.findById(6L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);

        mockMvc.perform(get(TOKEN_VERIFICATION + VALIDATE_TOKEN).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.VALID_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void validateToken_WhenValidToken_ReturnsVERIFIED() throws Exception {
        User user = userService.findById(2L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);

        mockMvc.perform(get(TOKEN_VERIFICATION + VALIDATE_TOKEN).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.TOKEN_ALREADY_VERIFIED)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void validateToken_WhenExpiredToken_ReturnsEXPIRED() throws Exception {
        User user = userService.findById(5L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() - 360000000));
        tokenRepository.save(verificationToken);

        mockMvc.perform(get(TOKEN_VERIFICATION + VALIDATE_TOKEN).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.EXPIRED_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void validateToken_WhenInvalidToken_ReturnsINVALID() throws Exception {
        mockMvc.perform(get(TOKEN_VERIFICATION + VALIDATE_TOKEN).param("token", "non-existent-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.INVALID_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void saveVerificationTokenForUser_WhenValidRequest_ReturnsSuccess() throws Exception {
        User user = new User();
        user.setId(3L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        VerificationTokenRequest request = new VerificationTokenRequest();
        request.setUser(user);
        request.setToken(token);

        mockMvc.perform(post(TOKEN_VERIFICATION + SAVE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.TOKEN_SAVED_SUCCESS)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteUserToken_WhenValidUserId_ReturnsSuccess() throws Exception {
        Long userId = 8L;

        mockMvc.perform(delete(TOKEN_VERIFICATION + DELETE_TOKEN).param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.TOKEN_DELETE_SUCCESS)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void generateNewVerificationToken_WhenValidOldToken_ReturnsNewToken() throws Exception {
        User user = userService.findById(6L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);

        mockMvc.perform(put(TOKEN_VERIFICATION + GENERATE_NEW_TOKEN_FOR_USER).param("oldToken", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("")))
                .andExpect(jsonPath("$.data.token", notNullValue()));
    }

    @Test
    @Transactional
    void checkTokenExpiration_WhenTokenIsExpired_ReturnsExpiredResponse() throws Exception {
        User user = userService.findById(5L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() - 360000000));
        tokenRepository.save(verificationToken);

        mockMvc.perform(get(TOKEN_VERIFICATION + CHECK_TOKEN_EXPIRATION)
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.EXPIRED_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void checkTokenExpiration_WhenTokenIsValid_ReturnsValidResponse() throws Exception {
        User user = userService.findById(4L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() + 360000000));
        tokenRepository.save(verificationToken);

        mockMvc.perform(get(TOKEN_VERIFICATION + CHECK_TOKEN_EXPIRATION)
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.VALID_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}

