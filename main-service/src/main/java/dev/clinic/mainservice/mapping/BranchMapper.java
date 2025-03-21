package dev.clinic.mainservice.mapping;

import dev.clinic.mainservice.dtos.appointments.AppointmentResponseOwner;
import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.entities.Branches;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BranchMapper {
    public static Branches toRequest(BranchRequest branchRequest) {
        if (branchRequest == null) {
            return null;
        }
        Branches branch = new Branches();
        branch.setAddress(branchRequest.getAddress());
        branch.setPhone(branchRequest.getPhone());
        branch.setEmail(branchRequest.getEmail());
        branch.setCoordinates(branchRequest.getCoordinates());
        return branch;
    }

    public static BranchResponse toResponse(Branches branches) {
        if (branches == null) {
            return null;
        }
        BranchResponse branchResponse = new BranchResponse();
        branchResponse.setId(branches.getId());
        branchResponse.setAddress(branches.getAddress());
        branchResponse.setPhone(branches.getPhone());
        branchResponse.setEmail(branches.getEmail());
        branchResponse.setCoordinates(branches.getCoordinates());
        return branchResponse;
    }

    public static List<BranchResponse> toResponseList(List<Branches> branches) {
        if (branches == null) {
            return Collections.emptyList();
        }
        return branches.stream()
                .map(BranchMapper::toResponse)
                .collect(Collectors.toList());
    }
}
