package com.project.petcare.factory;

import com.project.petcare.model.User;
import com.project.petcare.repository.AdminRepository;
import com.project.petcare.repository.PetOwnerRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.repository.VeterinarianRepository;
import com.project.petcare.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimpleUserFactory implements UserFactory {

    private final UserRepository userRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final AdminRepository adminRepository;


    @Override
    public User createUser(RegistrationRequest registrationRequest) {
        return null;
    }

}
