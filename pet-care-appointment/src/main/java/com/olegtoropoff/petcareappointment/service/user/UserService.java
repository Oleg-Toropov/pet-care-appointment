package com.olegtoropoff.petcareappointment.service.user;

import com.olegtoropoff.petcareappointment.factory.UserFactory;
import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserFactory userFactory;
    public User add(RegistrationRequest request) {
        return userFactory.createUser(request);
    }
}
