package com.project.petcare.factory;

import com.project.petcare.model.Admin;
import com.project.petcare.repository.AdminRepository;
import com.project.petcare.request.RegistrationRequest;
import com.project.petcare.service.role.IRoleService;
import com.project.petcare.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminFactory {

    private final AdminRepository adminRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;


    public Admin createAdmin(RegistrationRequest request) {
        Admin admin = new Admin();
        admin.setRoles(roleService.setUserRole("ADMIN"));
        userAttributesMapper.setCommonAttributes(request, admin);
        return adminRepository.save(admin);
    }

}
