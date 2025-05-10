package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.users.DoctorResponse;
import dev.clinic.mainservice.models.entities.Doctor;

public class DoctorMapping {
    public static DoctorResponse toResponse(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getRole().getName().getName(),
                doctor.getEmail(),
                doctor.getNumberPhone(),
                doctor.getSpecialization(),
                doctor.getAddress(),
                doctor.getEducation(),
                doctor.getPhoto()
        );
    }
}
