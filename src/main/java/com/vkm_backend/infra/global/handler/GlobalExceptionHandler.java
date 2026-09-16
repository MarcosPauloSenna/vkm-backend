package com.vkm_backend.infra.global.handler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler {

    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException (BusinessException ex){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),ex.getLocal(), ex.getMessage(), "BUSINESS_EXCEPTION");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validationException (ValidationException ex){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),201, ex.getMessage(), "VALIDATION_EXCEPTION");
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
                LocalDateTime.now(ZONE),
                400,
                message,
                "VALIDATION_EXCEPTION"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<ErrorResponse> jwtCreationException(JWTCreationException ex, HttpServletRequest request){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),400, ex.getMessage(), "JWT_CRREATION_EXCEPTION. LOCAL: "
                +request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> jwtValidationException(JWTVerificationException ex, HttpServletRequest request){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),400, ex.getMessage(), "JWT_VERIFICATION_EXCEPTION. LOCAL: "
                +request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> credentialsValidationException(BadCredentialsException ex){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),403, ex.getMessage(), "CREDENTIAL_VERIFICATION_EXCEPTION");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> authenticationValidationException(InternalAuthenticationServiceException ex){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),403, "usuário não é encontrado", "AUTHENTICATION_VERIFICATION_EXCEPTION");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}
