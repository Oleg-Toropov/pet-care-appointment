package com.olegtoropoff.petcareappointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import com.olegtoropoff.petcareappointment.utils.JwtTestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.olegtoropoff.petcareappointment.utils.UrlMapping.*;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clean_database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/test_pet_care_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTestUtils jwtTestUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveReview_WithValidToken_ReturnsSuccess() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post(REVIEWS + SUBMIT_REVIEW)
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "2")
                        .param("veterinarianId", "7")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.REVIEW_SUBMIT_SUCCESS));
    }

    @Test
    void saveReview_ThrowsIllegalArgumentException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post(REVIEWS + SUBMIT_REVIEW)
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "7")
                        .param("veterinarianId", "7")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.CANNOT_REVIEW))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void saveReview_ThrowsAlreadyExistsException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post(REVIEWS + SUBMIT_REVIEW)
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "2")
                        .param("veterinarianId", "8")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.ALREADY_REVIEWED))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void saveReview_ThrowsResourceNotFoundException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post(REVIEWS + SUBMIT_REVIEW)
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "100")
                        .param("veterinarianId", "8")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void saveReview_ThrowsIllegalStateException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post(REVIEWS + SUBMIT_REVIEW)
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "2")
                        .param("veterinarianId", "11")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.REVIEW_NOT_ALLOWED))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void deleteReview_ReturnsSuccess() throws Exception {
        mockMvc.perform(delete(REVIEWS + DELETE_REVIEW, 4L)
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.REVIEW_DELETE_SUCCESS));
    }

    @Test
    void deleteReview_ThrowsResourceNotFoundException() throws Exception {
        mockMvc.perform(delete(REVIEWS + DELETE_REVIEW, 100L)
                        .header("Authorization", jwtTestUtils.generateDefaultToken("alexey@gmail.com", 2L, "ROLE_PATIENT")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(FeedBackMessage.REVIEW_NOT_FOUND));
    }
}
