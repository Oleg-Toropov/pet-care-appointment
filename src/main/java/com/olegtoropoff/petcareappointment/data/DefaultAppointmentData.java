package com.olegtoropoff.petcareappointment.data;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class DefaultAppointmentData {
    private List<AppointmentData> appointments;

    @Data
    public static class AppointmentData {
        private String reason;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String appointmentNo;
        private String status;
        private String patientEmail;
        private String veterinarianEmail;
        private List<PetData> pets;
    }

    @Data
    public static class PetData {
        private String name;
        private String type;
        private String color;
        private String breed;
        private int age;
    }
}
