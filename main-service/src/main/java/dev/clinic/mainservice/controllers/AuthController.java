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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Эндпоинты для регистрации, верификации, авторизации и обновления токенов")
public class AuthController {

    private final AuthServiceImpl authService;
    private final UserServiceImpl userService;

    @Autowired
    public AuthController(AuthServiceImpl authService, UserServiceImpl userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param request объект SignUpRequest, содержащий email, пароль и полное имя пользователя.
     * @return Сообщение о том, что регистрация инициирована и проверьте email для получения кода верификации.
     */
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает нового пользователя и отправляет код подтверждения на email для верификации."
    )
    @ApiResponse(responseCode = "200", description = "Регистрация успешно инициирована.")
    @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации или пользователь уже существует.")
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody
            @Parameter(description = "Данные для регистрации (email, пароль, полное имя)") SignUpRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Registration initiated. Check your email for verification code.");
    }

    /**
     * Верификация email (2FA).
     *
     * @param request объект VerifyRequest, содержащий email и код верификации.
     * @return Сообщение об успешной верификации.
     */
    @Operation(
            summary = "Верификация email",
            description = "Подтверждает email пользователя с помощью кода, отправленного на email."
    )
    @ApiResponse(responseCode = "200", description = "Пользователь успешно верифицирован.")
    @ApiResponse(responseCode = "400", description = "Некорректный код верификации или истек срок его действия.")
    @PostMapping("/verify")
    public ResponseEntity<String> verify(
            @RequestBody
            @Parameter(description = "Данные для верификации (email, код)") VerifyRequest request) {
        authService.verify(request);
        return ResponseEntity.ok("User verified successfully.");
    }

    /**
     * Авторизация пользователя.
     *
     * @param request объект SignInRequest, содержащий email и пароль.
     * @return Объект AuthResponse с access и refresh токенами.
     */
    @Operation(
            summary = "Авторизация пользователя",
            description = "Выполняет проверку учетных данных пользователя и возвращает JWT access и refresh токены."
    )
    @ApiResponse(responseCode = "200", description = "Авторизация успешна. Возвращает access и refresh токены.")
    @ApiResponse(responseCode = "401", description = "Неверные учетные данные.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody
            @Parameter(description = "Данные для входа (email и пароль)") SignInRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Обновление access токена по refresh токену.
     *
     * @param request объект RefreshRequest, содержащий refresh токен.
     * @return Объект AuthResponse с новым access и refresh токенами.
     */
    @Operation(
            summary = "Обновление токена",
            description = "Использует refresh токен для генерации нового access токена и обновления refresh токена."
    )
    @ApiResponse(responseCode = "200", description = "Токены успешно обновлены.")
    @ApiResponse(responseCode = "400", description = "Недействительный или неправильный refresh токен.")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody
            @Parameter(description = "Refresh токен") RefreshRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-context")
    public ResponseEntity<UserDetailResponse> getPrincipalUser() {
        try {
            return ResponseEntity.ok(userService.getPrincipalUser());
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
