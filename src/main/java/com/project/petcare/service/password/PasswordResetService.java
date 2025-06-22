package com.project.petcare.service.password;

import com.project.petcare.model.User;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PasswordResetService implements IPasswordResetService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public Optional<User> findUserByPasswordResetToken(String token) {
        return Optional.empty();
    }

    @Override
    public void requestPasswordReset(String email) {

    }

    @Override
    public String resetPassword(String password, User user) {
        return "";
    }
}
