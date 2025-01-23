package com.olegtoropoff.petcareappointment.repository;

public interface VeterinarianReviewProjection {
    Long getVeterinarianId();
    Double getAverageRating();
    Long getTotalReviewers();
}