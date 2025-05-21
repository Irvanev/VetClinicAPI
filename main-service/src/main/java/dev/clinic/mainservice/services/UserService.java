package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.auth.ChangePasswordRequest;
import dev.clinic.mainservice.dtos.users.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    UserDetailResponse getPrincipalUser();
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserByEmail(String email);
    UserResponse getUserById(Long id);
    UserResponse getUserByPetId(Long petId);
    void changePassword(ChangePasswordRequest changePasswordRequest);

    DoctorResponse createDoctor(DoctorRequest doctorRequest);
    List<DoctorResponseForSelectInAppointment> getAllDoctorsByBranchId(Long branchId);
    UserResponse editClientByAdmin(EditClientRequest editClientRequest, Long clientId, MultipartFile photo);
    UserProfileResponse editClient(EditClientRequest editClientRequest);
    UserProfileResponse editPhoto(MultipartFile photo);
}
