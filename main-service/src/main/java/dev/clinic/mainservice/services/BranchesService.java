package dev.clinic.mainservice.services;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;

public interface BranchesService {
    BranchResponse createBranch(BranchRequest branchRequest);
    BranchResponse getBranchById(Long id);
    BranchResponse editBranch(Long id, BranchRequest branchRequest);
    boolean deleteBranch(Long id);
}
