package dev.clinic.mainservice.dtos.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Получение данных пользователя при авторизации")
public class AuthResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;
    private int accessTokenExpiresIn;
    private int refreshTokenExpiresIn;

    public AuthResponse(Long id, String accessToken, String refreshToken, int accessTokenExpiresIn, int refreshTokenExpiresIn) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Schema(description = "Access токен")
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Schema(description = "Refresh токен")
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Schema(description = "Время жизни Access токена в секундах", example = "3600")
    public int getAccessTokenExpiresIn() {
        return accessTokenExpiresIn;
    }
    public void setAccessTokenExpiresIn(int accessTokenExpiresIn) {
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    @Schema(description = "Время жизни Refresh токена в секундах", example = "604800")
    public int getRefreshTokenExpiresIn() {
        return refreshTokenExpiresIn;
    }
    public void setRefreshTokenExpiresIn(int refreshTokenExpiresIn) {
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }
}