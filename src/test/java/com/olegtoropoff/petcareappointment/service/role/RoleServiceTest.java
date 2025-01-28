package com.olegtoropoff.petcareappointment.service.role;

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

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
