package com.project.petcare.service.role;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Role;
import com.project.petcare.repository.RoleRepository;
import com.project.petcare.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoles_shouldReturnList() {
        List<Role> roles = Arrays.asList(new Role(1L, "ROLE_VET", null), new Role(2L, "ROLE_ADMIN", null));
        when(roleRepository.findAll()).thenReturn(roles);
        List<Role> result = roleService.getAllRoles();
        assertEquals(2, result.size());
    }

    @Test
    void getRoleById_found_shouldReturnRole() {
        Role role = new Role();
        when(roleRepository.findById(5L)).thenReturn(Optional.of(role));
        Role result = roleService.getRoleById(5L);
        assertEquals(role, result);
    }

    @Test
    void getRoleById_notFound_shouldReturnNull() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        assertNull(roleService.getRoleById(1L));
    }

    @Test
    void getRoleByName_found_shouldReturnRole() {
        Role role = new Role();
        when(roleRepository.findByRoleName("ROLE_VET")).thenReturn(Optional.of(role));
        Role result = roleService.getRoleByName("ROLE_VET");
        assertEquals(role, result);
    }

    @Test
    void getRoleByName_notFound_shouldReturnNull() {
        when(roleRepository.findByRoleName("ROLE_UNKNOWN")).thenReturn(Optional.empty());
        assertNull(roleService.getRoleByName("ROLE_UNKNOWN"));
    }

    @Test
    void saveRole_shouldCallRepository() {
        Role role = new Role();
        roleService.saveRole(role);
        verify(roleRepository).save(role);
    }

    @Test
    void setUserRole_found_shouldReturnRoleSet() {
        Role role = new Role();
        when(roleRepository.findByRoleName("ROLE_VET")).thenReturn(Optional.of(role));
        Set<Role> result = roleService.setUserRole("VET");
        assertEquals(1, result.size());
        assertTrue(result.contains(role));
    }

    @Test
    void setUserRole_notFound_shouldThrow() {
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> roleService.setUserRole("ADMIN"));
        assertEquals(FeedBackMessage.ROLE_NOT_FOUND, ex.getMessage());
    }
}
