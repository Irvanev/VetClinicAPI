package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.branches.BranchRequest;
import dev.clinic.mainservice.dtos.branches.BranchResponse;
import dev.clinic.mainservice.services.BranchesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@Tag(name = "Филиалы", description = "Управление филиалами клиники")
public class BranchesController {

    private final BranchesService branchesService;

    @Autowired
    public BranchesController(BranchesService branchesService) {
        this.branchesService = branchesService;
    }

    @Operation(
            summary = "Получить все филиалы",
            description = "Возвращает список всех филиалов клиники",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список филиалов успешно получен"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
            }
    )
    @GetMapping
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        List<BranchResponse> branches = branchesService.getAllBranches();
        return new ResponseEntity<>(branches, HttpStatus.OK);
    }

    @Operation(
            summary = "Создать новый филиал",
            description = "Создаёт новый филиал клиники",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Филиал успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PostMapping
    public ResponseEntity<BranchResponse> createBranch(
            @Parameter(description = "Данные нового филиала", required = true)
            @Valid @RequestBody BranchRequest branchRequest
    ) {
        BranchResponse createdBranch = branchesService.createBranch(branchRequest);
        return new ResponseEntity<>(createdBranch, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Получить филиал по ID",
            description = "Возвращает информацию о конкретном филиале по его ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Филиал найден"),
                    @ApiResponse(responseCode = "404", description = "Филиал не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getBranchById(
            @Parameter(description = "ID филиала", required = true)
            @PathVariable Long id
    ) {
        BranchResponse branch = branchesService.getBranchById(id);
        return new ResponseEntity<>(branch, HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать филиал",
            description = "Обновляет информацию о филиале по его ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Филиал успешно обновлён"),
                    @ApiResponse(responseCode = "404", description = "Филиал не найден")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> editBranch(
            @Parameter(description = "ID филиала", required = true)
            @PathVariable Long id,

            @Parameter(description = "Новые данные филиала", required = true)
            @Valid @RequestBody BranchRequest branchRequest
    ) {
        BranchResponse updatedBranch = branchesService.editBranch(id, branchRequest);
        return new ResponseEntity<>(updatedBranch, HttpStatus.OK);
    }

    @Operation(
            summary = "Удалить филиал",
            description = "Удаляет филиал по его ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Филиал успешно удалён"),
                    @ApiResponse(responseCode = "404", description = "Филиал не найден")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(
            @Parameter(description = "ID филиала", required = true)
            @PathVariable Long id
    ) {
        branchesService.deleteBranch(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
