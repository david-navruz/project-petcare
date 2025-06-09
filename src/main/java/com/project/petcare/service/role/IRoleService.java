package com.project.petcare.service.role;

import com.project.petcare.model.Role;
import java.util.List;
import java.util.Set;

public interface IRoleService {

    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role getRoleByName(String roleName);

    void saveRole(Role role);

    Set<Role> setUserRole(String userType);

}
