package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;

import java.util.List;

public interface BranchesService {
    List<BranchResponse> getAllBranches();
    BranchResponse createBranch(BranchRequest branchRequest);
    BranchResponse getBranchById(Long id);
    BranchResponse editBranch(Long id, BranchRequest branchRequest);
    boolean deleteBranch(Long id);
}
