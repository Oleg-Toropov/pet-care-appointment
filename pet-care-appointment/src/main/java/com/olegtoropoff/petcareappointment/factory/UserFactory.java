package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.User;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;

public interface UserFactory {
    public User createUser(RegistrationRequest request);
}
