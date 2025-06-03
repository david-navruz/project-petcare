package com.project.petcare.service.user;

import com.project.petcare.model.User;
import com.project.petcare.request.RegistrationRequest;

public interface IUserService {

    public User createNewUser(RegistrationRequest request);

}
