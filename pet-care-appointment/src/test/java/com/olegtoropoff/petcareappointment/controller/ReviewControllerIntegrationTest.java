package com.olegtoropoff.petcareappointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegtoropoff.petcareappointment.model.Review;
import com.olegtoropoff.petcareappointment.utils.JwtTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        mockMvc.perform(post("/api/v1/reviews/submit-review")
                        .header("Authorization", jwtTestUtils.generateDefaultToken(2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "2")
                        .param("veterinarianId", "7")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Отзыв успешно отправлен"));
    }

    @Test
    void saveReview_ThrowsIllegalArgumentException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post("/api/v1/reviews/submit-review")
                        .header("Authorization", jwtTestUtils.generateDefaultToken(2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "7")
                        .param("veterinarianId", "7")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value("Ветеринары не могут оставлять отзывы о себе"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void saveReview_ThrowsAlreadyExistsException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post("/api/v1/reviews/submit-review")
                        .header("Authorization", jwtTestUtils.generateDefaultToken(2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "2")
                        .param("veterinarianId", "8")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Вы уже оставили отзыв этому ветеринару, вы можете удалить предыдущий отзыв и написать новый"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void saveReview_ThrowsResourceNotFoundException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post("/api/v1/reviews/submit-review")
                        .header("Authorization", jwtTestUtils.generateDefaultToken(2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "100")
                        .param("veterinarianId", "8")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ветеринар или пациент не найдены"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void saveReview_ThrowsIllegalStateException() throws Exception {
        Review review = new Review();
        review.setStars(5);
        review.setFeedback("Great service!");

        mockMvc.perform(post("/api/v1/reviews/submit-review")
                        .header("Authorization", jwtTestUtils.generateDefaultToken(2L, "ROLE_PATIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("reviewerId", "2")
                        .param("veterinarianId", "11")
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value("Извините, оставить отзыв могут только пациенты, у которых была завершенная запись с этим ветеринаром"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void deleteReview_ReturnsSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/reviews/review/4/delete")
                        .header("Authorization", jwtTestUtils.generateDefaultToken(2L, "ROLE_PATIENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Отзыв успешно удален"));
    }

    @Test
    void deleteReview_ThrowsResourceNotFoundException() throws Exception {
        mockMvc.perform(delete("/api/v1/reviews/review/100/delete")
                        .header("Authorization", jwtTestUtils.generateDefaultToken(2L, "ROLE_PATIENT")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Отзыв(ы) не найден(ы)"));
    }
}
