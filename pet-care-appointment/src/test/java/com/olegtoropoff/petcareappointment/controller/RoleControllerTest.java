package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RoleControllerTest {

    @InjectMocks
    private RoleController roleController;

    @Mock
    private IRoleService roleService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void getAllRoles_ReturnsListOfRoles() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_ADMIN");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_PATIENT");

        Role role3 = new Role();
        role3.setId(3L);
        role3.setName("ROLE_VET");

        List<Role> roles = Arrays.asList(role1, role2, role3);
        when(roleService.getAllRoles()).thenReturn(roles);

        List<Role> result = roleController.getAllRoles();

        assertEquals(roles, result);
        verify(roleService, times(1)).getAllRoles();
    }

    @Test
    void getRoleById_ReturnsRole() {
        Long roleId = 1L;
        Role role= new Role();
        role.setId(roleId);
        role.setName("ROLE_ADMIN");

        when(roleService.getRoleById(roleId)).thenReturn(role);

        Role result = roleController.getRoleById(roleId);

        assertEquals(role, result);
        verify(roleService, times(1)).getRoleById(roleId);
    }

    @Test
    void getRoleByName_ReturnsRole() {
        String roleName = "ROLE_ADMIN";
        Role role= new Role();
        role.setId(1L);
        role.setName(roleName);
        when(roleService.getRoleByName(roleName)).thenReturn(role);

        Role result = roleController.getRoleByName(roleName);

        assertEquals(role, result);
        verify(roleService, times(1)).getRoleByName(roleName);
    }
}