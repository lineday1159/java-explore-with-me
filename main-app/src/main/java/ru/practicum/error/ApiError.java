package ru.practicum.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@ToString
public class ApiError {
    private final StackTraceElement[] errors;
    private final String message;
    private final String reason;
    private final String status;
    private final String timestamp;
}
