package ru.practicum.error;

import lombok.Data;

@Data
public class NotFoundException extends RuntimeException {
    private final String message;
    private final String reason;

    public NotFoundException(String message, String reason) {
        super();
        this.message = message;
        this.reason = reason;
    }
}
