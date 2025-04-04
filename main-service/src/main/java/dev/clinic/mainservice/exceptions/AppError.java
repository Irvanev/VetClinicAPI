package dev.clinic.mainservice.exceptions;

import java.time.LocalDateTime;

public class AppError {
    private int statusCode;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public AppError() {
    }

    public AppError(int statusCode, String message, String path, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
