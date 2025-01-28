package com.olegtoropoff.petcareappointment.service.role;

import com.olegtoropoff.petcareappointment.model.Role;

import java.util.Set;

/**
 * Interface for managing roles in the application.
 * Provides methods to retrieve roles by ID, name, and assign roles based on user types.
 */
public interface IRoleService {

    /**
     * Retrieves a role by its name.
     *
     * @param roleName the name of the role
     * @return the role with the given name, or {@code null} if not found
     */
    Role getRoleByName(String roleName);

    /**
     * Assigns roles to a user based on their user type.
     *
     * @param userType the type of the user (e.g., "ADMIN", "PATIENT", "VET")
     * @return a set of roles assigned to the user
     */
    Set<Role> setUserRole(String userType);
}