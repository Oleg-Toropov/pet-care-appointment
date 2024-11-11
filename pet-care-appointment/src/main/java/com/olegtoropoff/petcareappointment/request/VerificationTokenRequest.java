package com.olegtoropoff.petcareappointment.request;

import com.olegtoropoff.petcareappointment.model.User;
import lombok.Data;

import java.util.Date;

@Data
public class VerificationTokenRequest {
    private String token;
    private Date expirationTime;
    private User user;
}