package com.olegtoropoff.petcareappointment.factory;

import com.olegtoropoff.petcareappointment.model.Veterinarian;
import com.olegtoropoff.petcareappointment.repository.VeterinarianRepository;
import com.olegtoropoff.petcareappointment.request.RegistrationRequest;
import com.olegtoropoff.petcareappointment.service.role.IRoleService;
import com.olegtoropoff.petcareappointment.service.user.UserAttributesMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
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
     * Creates and persists a new {@link Veterinarian} instance based on the provided registration request.
     * This method sets the user's role to "VET", maps common attributes from the request to the veterinarian,
     * and assigns the specialization. The newly created veterinarian is then saved in the database.
     *
     * <p>Additionally, this method clears the caches associated with veterinarians and specializations,
     * to ensure that any newly added veterinarian is properly reflected in future cache-dependent queries.
     *
     * @param request the {@link RegistrationRequest} containing the details needed to create a new veterinarian,
     *                including common user attributes and specialization
     * @return the newly created and persisted {@link Veterinarian} instance
     */
    @CacheEvict(value = {"veterinarians_with_details", "specializations"}, allEntries = true)
    public Veterinarian createVeterinarian(RegistrationRequest request) {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setRoles(roleService.setUserRole("VET"));
        userAttributesMapper.setCommonAttributes(request, veterinarian);
        veterinarian.setClinicAddress(request.getClinicAddress());
        veterinarian.setAppointmentCost(request.getAppointmentCost());
        veterinarian.setSpecialization(request.getSpecialization());
        return veterinarianRepository.save(veterinarian);
    }
}
