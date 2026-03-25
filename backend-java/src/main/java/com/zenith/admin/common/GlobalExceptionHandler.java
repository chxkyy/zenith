package com.zenith.admin.common;

import com.alibaba.cola.dto.Response;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Response.buildFailure("CLIENT_ERROR", message);
    }

    @ExceptionHandler(BindException.class)
    public Response handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return Response.buildFailure("CLIENT_ERROR", message);
    }

    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        return Response.buildFailure("SERVER_ERROR", e.getMessage());
    }
}
