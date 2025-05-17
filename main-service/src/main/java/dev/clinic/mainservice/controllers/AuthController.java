package dev.clinic.mainservice.controllers;

import dev.clinic.mainservice.dtos.auth.AuthResponse;
import dev.clinic.mainservice.dtos.auth.RefreshRequest;
import dev.clinic.mainservice.dtos.auth.SignInRequest;
import dev.clinic.mainservice.dtos.auth.SignUpRequest;
import dev.clinic.mainservice.dtos.auth.VerifyRequest;
import dev.clinic.mainservice.dtos.users.UserDetailResponse;
import dev.clinic.mainservice.services.impl.AuthServiceImpl;
import dev.clinic.mainservice.services.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Управление регистрацией, входом, верификацией и обновлением токенов")
public class AuthController {

    private final AuthServiceImpl authService;
    private final UserServiceImpl userService;

    public AuthController(AuthServiceImpl authService, UserServiceImpl userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя и отправляет код подтверждения на email.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Регистрация успешно инициирована"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные или пользователь уже существует")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Parameter(description = "Данные для регистрации", required = true)
            @Valid @RequestBody SignUpRequest request
    ) {
        authService.register(request);
        return new ResponseEntity<>("Registration initiated. Check your email for verification code.", HttpStatus.CREATED);
    }

    @Operation(
            summary = "Верификация пользователя",
            description = "Подтверждает email пользователя по полученному коду.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользователь успешно верифицирован"),
                    @ApiResponse(responseCode = "400", description = "Неверный или просроченный код верификации")
            }
    )
    @PostMapping("/verify")
    public ResponseEntity<String> verify(
            @Parameter(description = "Данные для верификации", required = true)
            @Valid @RequestBody VerifyRequest request
    ) {
        authService.verify(request);
        return new ResponseEntity<>("User verified successfully.", HttpStatus.OK);
    }

    @Operation(
            summary = "Авторизация пользователя",
            description = "Проверяет учетные данные и возвращает JWT access и refresh токены.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Авторизация прошла успешно"),
                    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Данные для входа", required = true)
            @Valid @RequestBody SignInRequest request
    ) {
        AuthResponse response = authService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Обновление токенов",
            description = "Генерирует новый access токен на основе refresh токена.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
                    @ApiResponse(responseCode = "400", description = "Неверный или недействительный refresh токен")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Parameter(description = "Данные с refresh токеном", required = true)
            @Valid @RequestBody RefreshRequest request
    ) {
        AuthResponse response = authService.refresh(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает данные пользователя, основываясь на текущем контексте безопасности.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение данных"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            }
    )
    @GetMapping("/user")
    public ResponseEntity<UserDetailResponse> getPrincipalUser() {
        UserDetailResponse response = userService.getPrincipalUser();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
