package com.olegtoropoff.petcareappointment.service.patient;

import com.olegtoropoff.petcareappointment.dto.UserDto;

import java.util.List;

public interface IPatientService {
    List<UserDto> getPatients();
}
