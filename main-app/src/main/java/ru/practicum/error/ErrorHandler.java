package ru.practicum.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleIncorrectParameterException(final NotFoundException e) {
        return new ApiError(e.getStackTrace(), e.getMessage(), e.getReason(), HttpStatus.NOT_FOUND.getReasonPhrase(), new Date().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final ConflictException e) {
        return new ApiError(e.getStackTrace(), e.getMessage(), e.getReason(), HttpStatus.CONFLICT.getReasonPhrase(), new Date().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConflictException(final BadRequestException e) {
        return new ApiError(e.getStackTrace(), e.getMessage(), e.getReason(), HttpStatus.CONFLICT.getReasonPhrase(), new Date().toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final DataIntegrityViolationException e) {
        return new ApiError(e.getStackTrace(), e.getCause().getMessage(), e.getLocalizedMessage(), HttpStatus.CONFLICT.getReasonPhrase(), new Date().toString());
    }
}
