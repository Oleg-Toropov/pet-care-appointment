package com.olegtoropoff.petcareappointment.request;

import com.olegtoropoff.petcareappointment.model.Appointment;
import com.olegtoropoff.petcareappointment.model.Pet;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookAppointmentRequest {
    private Appointment appointment;
    private List<Pet> pets;

}
