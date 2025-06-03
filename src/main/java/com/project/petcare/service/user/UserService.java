package com.project.petcare.service.user;

import com.project.petcare.factory.UserFactory;
import com.project.petcare.model.User;
import com.project.petcare.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserFactory userFactory;


    @Override
    public User createNewUser(RegistrationRequest request) {
        return userFactory.createUser(request);
    }


}
