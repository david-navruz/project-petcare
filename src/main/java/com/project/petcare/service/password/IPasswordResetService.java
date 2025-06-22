package com.project.petcare.service.password;

import com.project.petcare.model.User;

import java.util.Optional;

public interface IPasswordResetService {
    Optional<User> findUserByPasswordResetToken(String token);
    void requestPasswordReset(String email);
    String resetPassword(String password, User user);
}
