package com.olegtoropoff.petcareappointment.service.vetbiography;

import com.olegtoropoff.petcareappointment.model.VetBiography;

public interface IVetBiographyService {
    VetBiography getVetBiographyByVetId(Long veterinarianId);

    VetBiography saveVetBiography(VetBiography veterinarianBio, Long vetId);

    VetBiography updateVetBiography(VetBiography vetBiography, Long id);

    void deleteVetBiography(Long id);
}
