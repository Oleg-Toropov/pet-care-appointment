package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Role;
import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
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
class VeterinarianFactoryTest {

    @Mock
    private VeterinarianRepository veterinarianRepository;

    @Mock
    private UserAttributesMapper userAttributesMapper;

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private VeterinarianFactory veterinarianFactory;

    @Test
    void createVeterinarian_ShouldCreateAndSaveVeterinarian() {
        RegistrationRequest request = new RegistrationRequest();
        request.setSpecialization("Dermatology");

        Role vetRole = new Role();
        vetRole.setName("VET");

        when(roleService.setUserRole("VET")).thenReturn(Set.of(vetRole));
        Veterinarian savedVet = new Veterinarian();
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(savedVet);

        Veterinarian result = veterinarianFactory.createVeterinarian(request);

        verify(roleService, times(1)).setUserRole("VET");
        verify(userAttributesMapper, times(1)).setCommonAttributes(eq(request), any(Veterinarian.class));
        ArgumentCaptor<Veterinarian> vetCaptor = ArgumentCaptor.forClass(Veterinarian.class);
        verify(veterinarianRepository, times(1)).save(vetCaptor.capture());

        assertEquals("Dermatology", vetCaptor.getValue().getSpecialization());
        assertEquals(savedVet, result);
    }
}
