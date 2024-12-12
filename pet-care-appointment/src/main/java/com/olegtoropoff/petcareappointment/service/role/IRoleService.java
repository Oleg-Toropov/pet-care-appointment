package com.olegtoropoff.petcareappointment.service.role;

import com.olegtoropoff.petcareappointment.model.Role;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    List<Role> getAllRoles();

    Role getRoleById(Long id);

    Role getRoleByName(String roleName);

    Set<Role> setUserRole(String userType);
}