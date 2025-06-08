package com.project.petcare.service.petowner;

import com.project.petcare.dto.UserDTO;

import java.util.List;

public interface IPetOwnerService {
    List<UserDTO> getPetOwners();
}
