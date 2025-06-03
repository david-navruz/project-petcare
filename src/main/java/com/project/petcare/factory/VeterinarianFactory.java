package com.project.petcare.factory;

import com.project.petcare.model.Veterinarian;
import com.project.petcare.repository.VeterinarianRepository;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.service.role.IRoleService;
import com.project.petcare.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VeterinarianFactory {

    private final VeterinarianRepository veterinarianRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;

    public Veterinarian createVeterinarian(RegistrationRequest request) {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setRoles(roleService.setUserRole("VET"));
        userAttributesMapper.setCommonAttributes(request, veterinarian);
        veterinarian.setSpecialization(request.getSpecialization());
        return veterinarianRepository.save(veterinarian);
    }

}
