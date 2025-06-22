package com.project.petcare.security.user;

import com.project.petcare.model.User;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UPCUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND));
        return UPCUserDetails.buildUserDetails(user);
    }

}
