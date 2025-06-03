package com.project.petcare.factory;

import com.project.petcare.model.User;
import com.project.petcare.request.RegistrationRequest;

public interface UserFactory {
    public User createNewUser(RegistrationRequest registrationRequest);
}
