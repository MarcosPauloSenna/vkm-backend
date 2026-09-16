package com.vkm_backend.global.handler;

import com.vkm_backend.global.exceptions.BusinessException;
import com.vkm_backend.global.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException (BusinessException ex){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(),ex.getLocal(), ex.getMessage(), "BUSINESS_EXCEPTION");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validationException (ValidationException ex){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(),201, ex.getMessage(), "VALIDATION_EXCEPTION");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .findFirst()
                .orElse("Dados inválidos.");

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                message,
                "VALIDATION_EXCEPTION"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

}
