package com.olegtoropoff.petcareappointment.data;

import lombok.Data;

import java.util.List;

@Data
public class DefaultReviewData {
    private List<ReviewData> reviews;
    @Data
    public static class ReviewData {
        private String feedback;
        private int stars;
        private String patientEmail;
        private String veterinarianEmail;
    }
}
