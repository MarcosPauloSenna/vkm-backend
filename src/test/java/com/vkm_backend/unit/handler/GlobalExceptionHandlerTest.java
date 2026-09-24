package com.vkm_backend.unit.handler;

import com.vkm_backend.infra.global.exceptions.BusinessException;
import com.vkm_backend.infra.global.exceptions.ConflictException;
import com.vkm_backend.infra.global.exceptions.EncryptionException;
import com.vkm_backend.infra.global.exceptions.ForbiddenOperationException;
import com.vkm_backend.infra.global.exceptions.ResourceNotFoundException;
import com.vkm_backend.infra.global.exceptions.ValidationException;
import com.vkm_backend.infra.global.handler.ErrorResponse;
import com.vkm_backend.infra.global.handler.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler(mock(MessageSource.class));
        request = new MockHttpServletRequest("GET", "/test");
    }

    @Test
    void shouldHandleBusinessExceptionAsBadRequest() {
        ResponseEntity<ErrorResponse> response = handler.businessException(
                new BusinessException("Regra de negócio inválida"),
                request
        );

        assertError(response, 400, "BUSINESS_EXCEPTION");
    }

    @Test
    void shouldHandleValidationExceptionAsUnauthorized() {
        ResponseEntity<ErrorResponse> response = handler.validationException(
                new ValidationException("Credencial inválida"),
                request
        );

        assertError(response, 401, "VALIDATION_EXCEPTION");
    }

    @Test
    void shouldHandleForbiddenOperationAsForbidden() {
        ResponseEntity<ErrorResponse> response = handler.forbiddenOperationException(
                new ForbiddenOperationException("Acesso negado"),
                request
        );

        assertError(response, 403, "FORBIDDEN_OPERATION");
    }

    @Test
    void shouldHandleResourceNotFoundAsNotFound() {
        ResponseEntity<ErrorResponse> response = handler.resourceNotFoundException(
                new ResourceNotFoundException("Recurso não encontrado"),
                request
        );

        assertError(response, 404, "RESOURCE_NOT_FOUND");
    }

    @Test
    void shouldHandleConflictAsConflict() {
        ResponseEntity<ErrorResponse> response = handler.conflictException(
                new ConflictException("Conflito de dados"),
                request
        );

        assertError(response, 409, "CONFLICT_EXCEPTION");
    }

    @Test
    void shouldHandleEncryptionFailureAsInternalServerError() {
        ResponseEntity<ErrorResponse> response = handler.encryptionException(
                new EncryptionException("Falha de criptografia"),
                request
        );

        assertError(response, 500, "ENCRYPION_EXCEPTION");
    }

    private void assertError(
            ResponseEntity<ErrorResponse> response,
            int expectedStatus,
            String expectedError
    ) {
        assertThat(response.getStatusCode().value()).isEqualTo(expectedStatus);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(expectedStatus);
        assertThat(response.getBody().error()).isEqualTo(expectedError);
    }
}
