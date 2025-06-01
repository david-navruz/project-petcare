package com.project.petcare.service.user;

import com.project.petcare.model.User;
import com.project.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;


    public void addUser(User user){
         userRepository.save(user);
    }

}
