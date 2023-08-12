package ru.practicum.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ApiError {
    private final StackTraceElement[] errors;
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;
}
