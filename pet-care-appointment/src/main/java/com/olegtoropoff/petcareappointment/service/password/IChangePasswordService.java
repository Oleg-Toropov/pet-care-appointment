package com.olegtoropoff.petcareappointment.service.password;

import com.olegtoropoff.petcareappointment.request.ChangePasswordRequest;

public interface IChangePasswordService {
    void changePassword(Long userId, ChangePasswordRequest request);
}
