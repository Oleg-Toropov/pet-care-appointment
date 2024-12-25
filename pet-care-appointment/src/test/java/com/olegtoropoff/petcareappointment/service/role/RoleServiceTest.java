package com.olegtoropoff.petcareappointment.service.role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.repository.RoleRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Tag("unit")
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRoles_Success() {
        Role role = new Role();
        when(roleRepository.findAll()).thenReturn(List.of(role));

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(role, result.get(0));

        verify(roleRepository).findAll();
    }

    @Test
    void getAllRoles_ReturnsEmptyList_WhenNoRolesExist() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(roleRepository).findAll();
    }

    @Test
    void getRoleById_Success() {
        Long roleId = 1L;
        Role role = new Role();
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(roleId);

        assertNotNull(result);
        assertEquals(role, result);

        verify(roleRepository).findById(roleId);
    }

    @Test
    void getRoleById_ReturnsNull_WhenRoleNotFound() {
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        Role result = roleService.getRoleById(roleId);

        assertNull(result);

        verify(roleRepository).findById(roleId);
    }

    @Test
    void getRoleByName_Success() {
        String roleName = "ROLE_ADMIN";
        Role role = new Role();
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleByName(roleName);

        assertNotNull(result);
        assertEquals(role, result);

        verify(roleRepository).findByName(roleName);
    }

    @Test
    void getRoleByName_ReturnsNull_WhenRoleNotFound() {
        String roleName = "ROLE_UNKNOWN";
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        Role result = roleService.getRoleByName(roleName);

        assertNull(result);

        verify(roleRepository).findByName(roleName);
    }

    @Test
    void setUserRole_Success() {
        String userType = "USER";
        Role role = new Role();
        when(roleRepository.findByName("ROLE_" + userType)).thenReturn(Optional.of(role));

        Set<Role> result = roleService.setUserRole(userType);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(role));

        verify(roleRepository).findByName("ROLE_" + userType);
    }

    @Test
    void setUserRole_ThrowsResourceNotFoundException_WhenRoleNotFound() {
        String userType = "UNKNOWN";
        when(roleRepository.findByName("ROLE_" + userType)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> roleService.setUserRole(userType));

        assertEquals(FeedBackMessage.ROLE_NOT_FOUND, exception.getMessage());

        verify(roleRepository).findByName("ROLE_" + userType);
    }
}
