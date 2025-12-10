package com.damvih.common.web.advice;

import com.damvih.common.web.dto.ErrorResponseDto;
import com.damvih.storage.exception.DirectoryAlreadyExistsException;
import com.damvih.storage.exception.InvalidPathException;
import com.damvih.storage.exception.ParentDirectoryNotFoundException;
import com.damvih.storage.exception.ResourceNotFoundException;
import com.damvih.authentication.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleDataIntegrityViolationException(UserAlreadyExistsException exception) {
        log.info(exception.getMessage());
        return new ErrorResponseDto("User already exists.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Validation failed.");

        return new ErrorResponseDto(message);
    }

    @ExceptionHandler(InvalidPathException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidPathException(InvalidPathException exception) {
        return new ErrorResponseDto("Invalid path.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.info(exception.getMessage());
        return new ErrorResponseDto("Resource not found.");
    }

    @ExceptionHandler(ParentDirectoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleParentDirectoryNotFoundException(ParentDirectoryNotFoundException exception) {
        log.info(exception.getMessage());
        return new ErrorResponseDto("Parent directory not found.");
    }

    @ExceptionHandler(DirectoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleDirectoryAlreadyExistsException(DirectoryAlreadyExistsException exception) {
        log.info(exception.getMessage());
        return new ErrorResponseDto("Directory already exists.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception exception) {
        log.info("Unknown exception: {}.", exception.getMessage());
        return new ErrorResponseDto("Internal server error.");
    }

}