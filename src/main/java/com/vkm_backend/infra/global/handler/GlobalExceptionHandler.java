package com.vkm_backend.infra.global.handler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.vkm_backend.infra.global.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private  final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException (BusinessException ex,  HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),HttpStatus.BAD_REQUEST.value() ,ex.getMessage(), "BUSINESS_EXCEPTION");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validationException (ValidationException ex,  HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),401, "Credencial inválida", "VALIDATION_EXCEPTION");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ErrorResponse> field = new ArrayList<>();
        String path = request.getDescription(false).replaceFirst("^uri=", "");
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", path);
        ex.getBindingResult().getFieldErrors().forEach(e -> {
            String message = messageSource.getMessage(e, LocaleContextHolder.getLocale());
            ErrorResponse error = new ErrorResponse(LocalDateTime.now(ZONE), status.value(), message, "METHOD_ARGUMENT_NOT_VALID_EXCEPTION" );
            field.add(error);
        });
        return ResponseEntity.status(status).body(field);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> dataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {

        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        log.error("Violação de integridade no banco de dados", ex);

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(ZONE),
                HttpStatus.CONFLICT.value(),
                "Não foi possível concluir a operação porque um dado informado já está cadastrado.",
                "DATA_INTEGRITY_VIOLATION"
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflictException(ConflictException ex, HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),HttpStatus.CONFLICT.value(), ex.getMessage(),"CONFLICT_EXCEPTION");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }


    @ExceptionHandler(JWTCreationException.class)
    public ResponseEntity<ErrorResponse> jwtCreationException(JWTCreationException ex, HttpServletRequest request){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),401, ex.getMessage(), "JWT_CRREATION_EXCEPTION.");
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ErrorResponse> jwtValidationException(JWTVerificationException ex, HttpServletRequest request){
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),401, ex.getMessage(), "JWT_VERIFICATION_EXCEPTION.");
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> credentialsValidationException(BadCredentialsException ex,  HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),HttpStatus.UNAUTHORIZED.value(), "Usuario ou Senha invalida!", "CREDENTIAL_VERIFICATION_EXCEPTION");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> authenticationValidationException(InternalAuthenticationServiceException ex,  HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),403, "usuário não encontrado", "AUTHENTICATION_VERIFICATION_EXCEPTION");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<ErrorResponse> encryptionException(EncryptionException ex,  HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),500, "usuário não encontrado", "ENCRYPION_EXCEPTION");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException ex,  HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),HttpStatus.NOT_FOUND.value(), ex.getMessage(),"RESOURCE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> forbiddenOperationException(ForbiddenOperationException ex,  HttpServletRequest request){
        String endPoint =  request.getRequestURI();
        logOrigin(ex);
        log.error("Endpoint da requisição: {}", endPoint);
        ErrorResponse error  = new ErrorResponse(LocalDateTime.now(ZONE),HttpStatus.FORBIDDEN.value(), ex.getMessage(),"FORBIDDEN_OPERATION");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    private void logOrigin(Exception ex) {
        StackTraceElement origin = findProjectOrigin(ex);

        if (origin == null) {
            log.error("Erro sem origem identificável", ex);
            return;
        }

        String source = origin.getClassName().replace('.', '/')
                + ".java:" + origin.getLineNumber();

        log.error(
                "Erro disparado em: {}. método={}",
                source,
                origin.getMethodName()
        );
    }

    private StackTraceElement findProjectOrigin(Exception ex) {
        for (StackTraceElement element : ex.getStackTrace()) {
            if (element.getClassName().startsWith("com.vkm_backend")) {
                return element;
            }
        }

        return ex.getStackTrace().length > 0
                ? ex.getStackTrace()[0]
                : null;
    }

}
