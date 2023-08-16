package ru.practicum.error;

import lombok.Data;

@Data
public class ConflictException extends RuntimeException {
    private final String message;
    private final String reason;

    public ConflictException(String message, String reason) {
        super();
        this.message = message;
        this.reason = reason;
    }
}
