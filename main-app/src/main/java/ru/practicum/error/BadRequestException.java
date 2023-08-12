package ru.practicum.error;

import lombok.Data;

@Data
public class BadRequestException extends RuntimeException {
    private final String message;
    private final String reason;

    public BadRequestException(String message, String reason) {
        super();
        this.message = message;
        this.reason = reason;
    }
}
