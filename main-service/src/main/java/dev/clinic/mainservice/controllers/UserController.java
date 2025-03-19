package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.auth.ChangePasswordRequest;
import dev.clinic.mainservice.dtos.users.DoctorRequest;
import dev.clinic.mainservice.dtos.users.DoctorResponse;
import dev.clinic.mainservice.dtos.users.DoctorResponseForSelectInAppointment;
import dev.clinic.mainservice.dtos.users.UserResponse;
import dev.clinic.mainservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        Page<UserResponse> userResponses = userService.getAllUsers(pageable);
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/user/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/pet/{petId}")
    public UserResponse getUserByPetId(@PathVariable Long petId) {
        return userService.getUserByPetId(petId);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/create-doctor")
    public UserResponse createDoctor(@RequestBody DoctorRequest doctorRequest) {
        return userService.createDoctor(doctorRequest);
    }

    @GetMapping("/doctors/{branchId}")
    public ResponseEntity<List<DoctorResponseForSelectInAppointment>> getDoctorsByScheduleId(@PathVariable Long branchId) {
        List<DoctorResponseForSelectInAppointment> doctors = userService.getAllDoctorsByBranchId(branchId);
        return ResponseEntity.ok(doctors);
    }
}
