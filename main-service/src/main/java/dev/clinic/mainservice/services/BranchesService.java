package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.models.enums.AppointmentType;

import java.util.List;

public interface BranchesService {
    List<BranchResponse> getAllBranches();
    List<BranchResponse> getAllBranchesByServiceName(AppointmentType service);
    BranchResponse createBranch(BranchRequest branchRequest);
    BranchResponse getBranchById(Long id);
    BranchResponse editBranch(Long id, BranchRequest branchRequest);
    void deleteBranch(Long id);
}
