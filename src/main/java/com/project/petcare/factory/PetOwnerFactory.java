package com.project.petcare.factory;

import com.project.petcare.model.PetOwner;
import com.project.petcare.repository.PetOwnerRepository;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.service.role.IRoleService;
import com.project.petcare.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PetOwnerFactory {

    private final PetOwnerRepository petOwnerRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;


    public PetOwner createPetOwner(RegistrationRequest request){
        PetOwner petOwner = new PetOwner();
        petOwner.setRoles(roleService.setUserRole("PETOWNER"));
        userAttributesMapper.setCommonAttributes(request, petOwner);
        return petOwnerRepository.save(petOwner);
    }

}
