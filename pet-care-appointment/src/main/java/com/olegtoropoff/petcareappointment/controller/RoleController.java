package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UrlMapping.ROLES)
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;

    @GetMapping(UrlMapping.GET_ALL_ROLES)
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping(UrlMapping.GET_ROLE_BY_ID)
    public Role getRoleById(Long id) {
        return roleService.getRoleById(id);
    }

    @GetMapping(UrlMapping.GET_ROLE_BY_NAME)
    public Role getRoleByName(String roleName) {
        return roleService.getRoleByName(roleName);
    }
}