package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Admin;
import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.repository.AdminRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class AdminFactoryTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserAttributesMapper userAttributesMapper;

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private AdminFactory adminFactory;

    @Test
    void createAdmin_ShouldCreateAndSaveAdmin() {
        RegistrationRequest request = new RegistrationRequest();
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        when(roleService.setUserRole("ADMIN")).thenReturn(Set.of(adminRole));
        Admin savedAdmin = new Admin();
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        Admin result = adminFactory.createAdmin(request);

        verify(roleService, times(1)).setUserRole("ADMIN");
        verify(userAttributesMapper, times(1)).setCommonAttributes(eq(request), any(Admin.class));
        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository, times(1)).save(adminCaptor.capture());
        assertEquals(savedAdmin, result);
    }
}
