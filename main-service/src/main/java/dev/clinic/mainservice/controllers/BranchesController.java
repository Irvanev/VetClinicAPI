package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.services.BranchesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/branches")
public class BranchesController {

    private final BranchesService branchesService;

    @Autowired
    public BranchesController(BranchesService branchesService) {
        this.branchesService = branchesService;
    }

    @PostMapping("/create-branch")
    public ResponseEntity<BranchResponse> createBranch(@RequestBody BranchRequest branchRequest) {
        try {
            BranchResponse branchResponse = branchesService.createBranch(branchRequest);
            return new ResponseEntity<>(branchResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get-branch/{id}")
    public ResponseEntity<BranchResponse> getBranch(@PathVariable Long id) {
        try {
            BranchResponse branchResponse = branchesService.getBranchById(id);
            return new ResponseEntity<>(branchResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/edit-branch/{id}")
    public ResponseEntity<BranchResponse> editBranch(@PathVariable Long id, @RequestBody BranchRequest branchRequest) {
        try {
            BranchResponse branchResponse = branchesService.editBranch(id, branchRequest);
            return new ResponseEntity<>(branchResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-branch/{id}")
    public ResponseEntity<HttpStatus> deleteBranch(@PathVariable Long id) {
        try {
            branchesService.deleteBranch(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
