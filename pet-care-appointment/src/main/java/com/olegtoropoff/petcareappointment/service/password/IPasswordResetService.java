package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.model.User;

import java.util.Optional;

public interface IPasswordResetService {
    User findUserByPasswordResetToken(String token, String password);

    void requestPasswordReset(String email);

    String resetPassword(String password, User user);
}
