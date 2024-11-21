package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.model.User;

import java.util.Optional;

public interface IPasswordResetService {
    Optional<User> findUserByPasswordResetToken(String token);

    void requestPasswordReset(String email);

    String resetPassword(String password, User user);
}
