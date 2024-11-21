package com.olegtoropoff.petcareappointment.request;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String token;
    private String newPassword;
}