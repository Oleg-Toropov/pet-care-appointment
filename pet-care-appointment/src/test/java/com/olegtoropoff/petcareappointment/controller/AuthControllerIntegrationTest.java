package com.olegtoropoff.petcareappointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegtoropoff.petcareappointment.config.TestConfig;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.rabbitmq.RabbitMQProducer;
import com.olegtoropoff.petcareappointment.repository.VerificationTokenRepository;
import com.olegtoropoff.petcareappointment.request.LoginRequest;
import com.olegtoropoff.petcareappointment.request.PasswordResetRequest;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
@Import(TestConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTestUtils jwtTestUtils;

    @Autowired
    private IVerificationTokenService tokenService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    @Test
    void login_WhenValidRequest_ReturnsJwt() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("alexey@gmail.com");
        loginRequest.setPassword("Password12345");

        mockMvc.perform(post(AUTH + LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.AUTHENTICATION_SUCCESS)))
                .andExpect(jsonPath("$.data.token", notNullValue()))
                .andExpect(jsonPath("$.data.id", notNullValue()));
    }

    @Test
    void login_WhenAccountDisabled_Returns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("anna@gmail.com");
        loginRequest.setPassword("Password12345");

        mockMvc.perform(post(AUTH + LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.ACCOUNT_DISABLED)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void login_WhenInvalidPassword_Returns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("alexey@gmail.com");
        loginRequest.setPassword("WrongPassword");

        mockMvc.perform(post(AUTH + LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", containsString("Bad credentials")))
                .andExpect(jsonPath("$.data", is(FeedBackMessage.INVALID_PASSWORD)));
    }

    @Test
    @Transactional
    void verifyEmail_WhenValidToken_ReturnsVALID() throws Exception {
        User user = userService.findById(6L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);

        mockMvc.perform(get(AUTH + VERIFY_EMAIL).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.VALID_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void verifyEmail_WhenAlreadyVerified_ReturnsTOKEN_ALREADY_VERIFIED() throws Exception {
        User user = userService.findById(2L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);

        mockMvc.perform(get(AUTH + VERIFY_EMAIL).param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.TOKEN_ALREADY_VERIFIED)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void verifyEmail_WhenExpiredToken_Returns410() throws Exception {
        User user = userService.findById(5L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpirationDate(new Date(System.currentTimeMillis() - 360000000));
        tokenRepository.save(verificationToken);

        mockMvc.perform(get(AUTH + VERIFY_EMAIL).param("token", token))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.EXPIRED_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void verifyEmail_WhenInvalidToken_Returns410() throws Exception {
        mockMvc.perform(get(AUTH + VERIFY_EMAIL)
                        .param("token", ""))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.INVALID_VERIFICATION_TOKEN)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void resendVerificationToken_WhenSuccess_ReturnsNEW_VERIFICATION_TOKEN_SENT() throws Exception {
        User user = userService.findById(2L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "ROLE_PATIENT");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        mockMvc.perform(put(AUTH + RESEND_VERIFICATION_TOKEN)
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.NEW_VERIFICATION_TOKEN_SENT)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void requestPasswordReset_WhenSuccess_ReturnsPASSWORD_RESET_EMAIL_SENT() throws Exception {
        Map<String, String> requestBody = Map.of("email", "igor@gmail.com");
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        mockMvc.perform(post(AUTH + REQUEST_PASSWORD_RESET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PASSWORD_RESET_EMAIL_SENT)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void requestPasswordReset_WhenUserNotFound_Returns400() throws Exception {
        String email = "test_email@gmail.com";
        Map<String, String> requestBody = Map.of("email", email);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        mockMvc.perform(post(AUTH + REQUEST_PASSWORD_RESET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(String.format(FeedBackMessage.USER_NOT_FOUND_WITH_EMAIL, email))))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void requestPasswordReset_WhenInvalidRequest_Returns400() throws Exception {
        String email = "invalid_email@gmail_com";
        Map<String, String> requestBody = Map.of("email", email);
        doNothing().when(rabbitMQProducer).sendMessage(anyString());

        mockMvc.perform(post(AUTH + REQUEST_PASSWORD_RESET)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.INVALID_EMAIL)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void resetPassword_WhenSuccess_ReturnsOk() throws Exception {
        User user = userService.findById(7L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "VET");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);

        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setToken(token);
        resetRequest.setNewPassword("NewPassword123");

        mockMvc.perform(post(AUTH + RESET_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.PASSWORD_RESET_SUCCESS)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @Transactional
    void resetPassword_WhenPasswordTooWeak_ReturnsBadRequest() throws Exception {
        User user = userService.findById(7L);
        String tokenWithBearer = jwtTestUtils.generateDefaultToken(user.getEmail(), user.getId(), "VET");
        String token = tokenWithBearer.substring(7);
        tokenService.saveVerificationTokenForUser(token, user);

        PasswordResetRequest resetRequest = new PasswordResetRequest();
        resetRequest.setToken(token);
        resetRequest.setNewPassword("weak");

        mockMvc.perform(post(AUTH + RESET_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(FeedBackMessage.INVALID_PASSWORD_FORMAT)))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
