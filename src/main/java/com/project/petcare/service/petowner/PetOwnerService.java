package com.project.petcare.service.petowner;

import com.project.petcare.dto.EntityConverter;
import com.project.petcare.dto.PetOwnerDTO;
import com.project.petcare.dto.UserDTO;
import com.project.petcare.model.PetOwner;
import com.project.petcare.model.User;
import com.project.petcare.repository.PetOwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PetOwnerService implements IPetOwnerService{

    private final PetOwnerRepository petOwnerRepository;
    private final EntityConverter<User, UserDTO> entityConverter;


    @Override
    public List<UserDTO> getPetOwners() {
        List<PetOwner> petOwnerList = petOwnerRepository.findAll();
        return petOwnerList.stream()
                .map( pet -> entityConverter.mapEntityToDTO(pet, UserDTO.class))
                .toList();
    }

}
