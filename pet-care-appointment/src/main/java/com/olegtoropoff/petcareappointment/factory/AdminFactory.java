package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Admin;
import com.olegtoropoff.petcareappointment.repository.AdminRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminFactory {
    private final AdminRepository adminRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;

    public Admin createAdmin(RegistrationRequest request) {
        Admin admin = new Admin();
        admin.setRoles(roleService.setUserRole("ADMIN"));
        userAttributesMapper.setCommonAttributes(request, admin);
        return adminRepository.save(admin);
    }
}
