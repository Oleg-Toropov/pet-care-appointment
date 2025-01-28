package com.olegtoropoff.petcareappointment.service.role;

import com.olegtoropoff.petcareappointment.exception.ResourceNotFoundException;
import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.repository.RoleRepository;
import com.olegtoropoff.petcareappointment.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class for managing roles in the application.
 * Provides functionality to retrieve roles by ID, name, and assign roles based on user types.
 */
@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;

    /**
     * Retrieves a role by its name.
     *
     * @param roleName the name of the role
     * @return the role with the given name, or {@code null} if not found
     */
    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }

    /**
     * Assigns a role to a user based on their user type.
     *
     * @param userType the type of the user (e.g., "ADMIN", "PATIENT", "VET")
     * @return a set of roles assigned to the user
     * @throws ResourceNotFoundException if the role corresponding to the user type is not found
     */
    @Override
    public Set<Role> setUserRole(String userType) {
        Set<Role> userRoles = new HashSet<>();
        roleRepository.findByName("ROLE_" + userType)
                .ifPresentOrElse(userRoles::add, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.ROLE_NOT_FOUND);
                });
        return userRoles;
    }
}
