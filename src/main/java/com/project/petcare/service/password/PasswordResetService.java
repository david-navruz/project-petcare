package com.project.petcare.service.password;

import com.project.petcare.event.PasswordResetEvent;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.User;
import com.project.petcare.model.VerificationToken;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.repository.VerificationTokenRepository;
import com.project.petcare.utils.FeedBackMessage;
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
        return tokenRepository.findByToken(token).map(VerificationToken::getUser);
    }

    @Override
    public void requestPasswordReset(String email) {
        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    PasswordResetEvent passwordResetEvent = new PasswordResetEvent(this, user);
                    eventPublisher.publishEvent(passwordResetEvent);
                }, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.NO_USER_FOUND + email);
                }
        );
    }

    @Override
    public String resetPassword(String password, User user) {
        try {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return FeedBackMessage.PASSWORD_RESET_SUCCESS;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
