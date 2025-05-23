package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.auth.ChangePasswordRequest;
import dev.clinic.mainservice.dtos.users.*;
import dev.clinic.mainservice.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Управление пользователями и врачами")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает пагинированный список всех пользователей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение списка"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Параметры пагинации")
            @ParameterObject Pageable pageable
    ) {
        return new ResponseEntity<>(userService.getAllUsers(pageable), HttpStatus.OK);
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает детальную информацию о пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь найден"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id
    ) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Найти владельца по питомцу",
            description = "Возвращает информацию о владельце по ID питомца",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные найдены"),
                    @ApiResponse(responseCode = "404", description = "Питомец не найден")
            }
    )
    @GetMapping("/by-pet/{petId}")
    public ResponseEntity<UserResponse> getUserByPetId(
            @Parameter(description = "ID питомца", required = true)
            @PathVariable Long petId
    ) {
        return new ResponseEntity<>(userService.getUserByPetId(petId), HttpStatus.OK);
    }

    @Operation(
            summary = "Смена пароля",
            description = "Обновление пароля текущего пользователя",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Пароль успешно изменен"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                    @ApiResponse(responseCode = "401", description = "Неавторизованный доступ")
            }
    )
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "Данные для смены пароля", required = true)
            @Valid @RequestBody ChangePasswordRequest request
            ) {
        userService.changePassword(request);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @Operation(
            summary = "Добавить врача",
            description = "Создание нового врача администратором",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Врач успешно создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PostMapping("/doctors")
    public ResponseEntity<DoctorResponse> createDoctor(
            @Parameter(description = "Данные нового врача", required = true)
            @Valid @RequestBody DoctorRequest doctorRequest) {
        return new ResponseEntity<>(userService.createDoctor(doctorRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Получить врачей филиала",
            description = "Список врачей по подразделению клиники",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список успешно получен"),
                    @ApiResponse(responseCode = "404", description = "Филиал не найден")
            }
    )
    @GetMapping("/branches/{branchId}/doctors")
    public ResponseEntity<List<DoctorResponseForSelectInAppointment>> getDoctorsByBranch(
            @Parameter(description = "ID филиала", required = true)
            @PathVariable Long branchId) {
        return new ResponseEntity<>(userService.getAllDoctorsByBranchId(branchId), HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать клиента администратором",
            description = "Обновление данных клиента от лица администратора",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Клиент не найден")
            }
    )
    @PutMapping(value = "/admin/clients/{clientId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateClientByAdmin(
            @Parameter(description = "ID клиента", required = true)
            @PathVariable Long clientId,

            @Parameter(description = "Данные для обновления")
            @Valid @RequestPart("data") EditClientRequest request,

            @Parameter(description = "Новое фото профиля")
            @RequestPart(value = "photo", required = false) MultipartFile photo
            ) {

        return new ResponseEntity<>(userService.editClientByAdmin(request, clientId, photo), HttpStatus.OK);
    }

    @Operation(
            summary = "Редактировать клиента",
            description = "Обновление данных клиента с возможностью загрузки фото",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Клиент не найден")
            }
    )
    @PutMapping(value = "/clients")
    public ResponseEntity<UserProfileResponse> updateClient(
            @Parameter(description = "Данные для обновления")
            @Valid EditClientRequest request
    ) {

        return new ResponseEntity<>(userService.editClient(request), HttpStatus.OK);
    }

    @Operation(
            summary = "Обновление фотографии пользователя",
            description = "Обновление фотографии пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Данные обновлены"),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Клиент не найден")
            }
    )
    @PutMapping(value = "/clients/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> editPhoto(
            @RequestPart("photo") MultipartFile photo
    ) {

        return new ResponseEntity<>(userService.editPhoto(photo), HttpStatus.OK);
    }
}
