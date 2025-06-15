package com.project.petcare.service.user;

import com.project.petcare.dto.UserDTO;
import com.project.petcare.model.User;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.request.UserUpdateRequest;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IUserService {

    public User createNewUser(RegistrationRequest request);

    public User updateUser(Long userId, UserUpdateRequest request);

    public User findUserById(Long userId);

    public void deleteUser(Long userId);

    public List<UserDTO> getAllUsers();

    UserDTO getUserWithDetails(Long userId) throws SQLException;

    long countVeterinarians();

    long countPetOwners();

    long countAllUsers();

    Map<String, Map<String,Long>> aggregateUsersByMonthAndType();

    Map<String, Map<String, Long>> aggregateUsersByEnabledStatusAndType();

}
