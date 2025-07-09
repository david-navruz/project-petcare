package com.project.petcare.factory;

import com.project.petcare.exception.UserAlreadyExistsException;
import com.project.petcare.model.User;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SimpleUserFactory implements UserFactory {

    private final UserRepository userRepository;
    private final VeterinarianFactory veterinarianFactory;
    private final PetOwnerFactory petOwnerFactory;
    private final AdminFactory adminFactory;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User createNewUser(RegistrationRequest registrationRequest) {
        if (userRepository.existsByEmail(registrationRequest.getEmail())){
            throw new UserAlreadyExistsException("Oops! "+registrationRequest.getEmail()+ " already exists!" );
        }
        // encoding the password
        registrationRequest.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

        switch (registrationRequest.getUserType()) {
            case "VET" -> {
                return veterinarianFactory.createVeterinarian(registrationRequest);
            }
            case "ADMIN" -> {
                return adminFactory.createAdmin(registrationRequest);
            }
            case "PETOWNER" -> {
                return petOwnerFactory.createPetOwner(registrationRequest);
            }
            default -> {
                return null;
            }
        }
    }

}
