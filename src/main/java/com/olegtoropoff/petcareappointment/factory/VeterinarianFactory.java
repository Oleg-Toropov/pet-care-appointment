package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Factory class for creating {@link Veterinarian} instances.
 * <p>
 * This factory handles the creation of veterinarians by mapping attributes from a
 * {@link RegistrationRequest}, assigning a "VET" role, and saving the veterinarian
 * to the database.
 */
@Service
@RequiredArgsConstructor
public class VeterinarianFactory {
    private final VeterinarianRepository veterinarianRepository;
    private final UserAttributesMapper userAttributesMapper;
    private final IRoleService roleService;

    /**
     * Creates a new {@link Veterinarian} instance based on the given registration request.
     *
     * @param request the registration request containing details for the veterinarian
     * @return the created {@link Veterinarian} instance, persisted in the database
     */
//   todo  @CacheEvict(value = {"veterinarians_with_details", "active_veterinarians", "all_veterinarians"}, allEntries = true)
    public Veterinarian createVeterinarian(RegistrationRequest request) {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setRoles(roleService.setUserRole("VET"));
        userAttributesMapper.setCommonAttributes(request, veterinarian);
        veterinarian.setSpecialization(request.getSpecialization());
        return veterinarianRepository.save(veterinarian);
    }
}
