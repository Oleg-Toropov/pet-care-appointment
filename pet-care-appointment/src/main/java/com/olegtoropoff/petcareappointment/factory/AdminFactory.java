package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Admin;
import com.olegtoropoff.petcareappointment.repository.AdminRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Factory class for creating {@link Admin} instances.
 * <p>
 * This factory is responsible for creating admin users by mapping attributes from
 * a {@link RegistrationRequest}, assigning the "ADMIN" role, and persisting the
 * admin to the database.
 */
@Service
@RequiredArgsConstructor
public class AdminFactory {
    private final AdminRepository adminRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;

    /**
     * Creates a new {@link Admin} instance based on the provided registration request.
     *
     * @param request the registration request containing details for the admin
     * @return the created {@link Admin} instance, persisted in the database
     */
    public Admin createAdmin(RegistrationRequest request) {
        Admin admin = new Admin();
        admin.setRoles(roleService.setUserRole("ADMIN"));
        userAttributesMapper.setCommonAttributes(request, admin);
        return adminRepository.save(admin);
    }
}
