package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.auth.ChangePasswordRequest;
import dev.clinic.mainservice.dtos.users.DoctorRequest;
import dev.clinic.mainservice.dtos.users.DoctorResponseForSelectInAppointment;
import dev.clinic.mainservice.dtos.users.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService {
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserByEmail(String email);
    UserResponse getUserById(Long id);
    UserResponse getUserByPetId(Long petId);
    void changePassword(ChangePasswordRequest changePasswordRequest);

    UserResponse createDoctor(DoctorRequest doctorRequest);
    List<DoctorResponseForSelectInAppointment> getAllDoctorsByBranchId(Long branchId);
}
