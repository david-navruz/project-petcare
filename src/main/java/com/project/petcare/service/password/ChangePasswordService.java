package com.project.petcare.service.password;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.User;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements IChangePasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void changePassword(Long userId, ChangePasswordRequest request ) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(Objects.equals(request.getCurrentPassword(), "")
                || Objects.equals(request.getNewPassword(), "")) {
            throw new IllegalArgumentException("All fields are required");
        }
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match");
        }
        if(!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Password confirmation mis-match ");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

    }
}
