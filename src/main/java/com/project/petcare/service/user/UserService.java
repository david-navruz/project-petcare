package com.project.petcare.service.user;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.factory.UserFactory;
import com.project.petcare.model.User;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.request.UserUpdateRequest;
import com.project.petcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {

    private final UserFactory userFactory;
    private final UserRepository userRepository;
    private final EntityConverter<User, UserDTO> entityConverter;


    @Override
    public User createNewUser(RegistrationRequest request) {
        return userFactory.createNewUser(request);
    }

    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {
        User userToUpdate = findUserById(userId);
        userToUpdate.setFirstName(request.getFirstName());
        userToUpdate.setLastName(request.getLastName());
        userToUpdate.setGender(request.getGender());
        userToUpdate.setPhoneNumber(request.getPhoneNumber());
        userToUpdate.setSpecialization(request.getSpecialization());
        return userRepository.save(userToUpdate);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NO_USER_FOUND));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> entityConverter.mapEntityToDTO(user, UserDTO.class))
                .collect(Collectors.toList());
    }

}
