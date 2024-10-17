package com.olegtoropoff.petcareappointment.service.veterinarian;

import com.olegtoropoff.petcareappointment.dto.UserDto;

import java.util.List;

public interface IVeterinarianService {
    List<UserDto> getAllVeterinariansWithDetails();
}
