package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.model.VerificationToken;
import com.olegtoropoff.petcareappointment.repository.VerificationTokenRepository;
import com.olegtoropoff.petcareappointment.service.user.UserService;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.JwtTestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.CHECK_TOKEN_EXPIRATION;
import static com.olegtoropoff.petcareappointment.utils.UrlMapping.TOKEN_VERIFICATION;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTestUtils jwtTestUtils;

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

