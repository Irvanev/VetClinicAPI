package dev.clinic.mainservice.services.impl;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.mapping.AppointmentMapper;
import dev.clinic.mainservice.mapping.BranchMapper;
import dev.clinic.mainservice.models.entities.Appointment;
import dev.clinic.mainservice.models.entities.Branches;
import dev.clinic.mainservice.models.enums.AppointmentType;
import dev.clinic.mainservice.repositories.BranchesRepository;
import dev.clinic.mainservice.services.BranchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchesServiceImpl implements BranchesService {

    private final BranchesRepository branchesRepository;

    @Autowired
    public BranchesServiceImpl(BranchesRepository branchesRepository) {
        this.branchesRepository = branchesRepository;
    }

    @Override
    @Cacheable("branchesList")
    public List<BranchResponse> getAllBranches() {
        List<Branches> branches = branchesRepository.findAll();
        return BranchMapper.toResponseList(branches);
    }

    @Override
    public List<BranchResponse> getAllBranchesByServiceName(AppointmentType service) {
        if (service == null) {
            throw new IllegalArgumentException("Service must not be null");
        }

        List<Branches> branches = branchesRepository.getAllBranchesByServicesContains(service);
        return branches.stream()
                .map(BranchMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"branches", "branchesList"}, allEntries = true)
    public BranchResponse createBranch(BranchRequest branchRequest) {
        Branches branch = BranchMapper.toRequest(branchRequest);

        Branches savedBranch = branchesRepository.save(branch);
        return BranchMapper.toResponse(savedBranch);
    }

    @Override
    @Cacheable(value = "branches", key = "#id")
    public BranchResponse getBranchById(Long id) {
        Branches branch = branchesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));

        return BranchMapper.toResponse(branch);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "branches", key = "#id"),
                    @CacheEvict(value = "branchesList", allEntries = true)
            }
    )
    public BranchResponse editBranch(Long id, BranchRequest branchRequest) {
        Branches branch = branchesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));

        BranchMapper.toRequest(branchRequest);
        return BranchMapper.toResponse(branchesRepository.save(branch));
    }

    @Override
    @Caching(
            evict = {
                @CacheEvict(value = "branches", key = "#id"),
                @CacheEvict(value = "branchesList", allEntries = true)
        }
    )
    public boolean deleteBranch(Long id) {
        Branches branch = branchesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
        branchesRepository.delete(branch);
        return true;
    }
}
