package com.olegtoropoff.petcareappointment.service.user;

import com.olegtoropoff.petcareappointment.dto.UserDto;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.request.UserUpdateRequest;

import java.util.List;

public interface IUserService {
    User register(RegistrationRequest request);

    User update(Long userId, UserUpdateRequest request);

    User findById(Long userId);

    void deleteById(Long userId);

    List<UserDto> getAllUsers();
}
