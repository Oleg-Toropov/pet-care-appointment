package com.olegtoropoff.petcareappointment.controller;

import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for managing roles in the system.
 */
@RestController
@RequestMapping(UrlMapping.ROLES)
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;

    /**
     * Retrieves a list of all roles in the system.
     *
     * @return a list of {@link Role} objects representing all roles
     */
    @GetMapping(UrlMapping.GET_ALL_ROLES)
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id the ID of the role to be retrieved
     * @return the {@link Role} object associated with the specified ID
     */
    @GetMapping(UrlMapping.GET_ROLE_BY_ID)
    public Role getRoleById(Long id) {
        return roleService.getRoleById(id);
    }

    /**
     * Retrieves a role by its name.
     *
     * @param roleName the name of the role to be retrieved
     * @return the {@link Role} object associated with the specified name
     */
    @GetMapping(UrlMapping.GET_ROLE_BY_NAME)
    public Role getRoleByName(String roleName) {
        return roleService.getRoleByName(roleName);
    }
}