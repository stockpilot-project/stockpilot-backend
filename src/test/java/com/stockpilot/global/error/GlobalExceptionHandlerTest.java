package com.stockpilot.global.error;

import com.stockpilot.global.common.response.ApiResponse;
import com.stockpilot.global.error.exception.BusinessException;
import com.stockpilot.global.error.exception.ErrorCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("MissingServletRequestParameterException → 400 / C005, 파라미터 이름 포함")
    void handleMissingParameter() {
        var ex = new MissingServletRequestParameterException("q", "String");

        ResponseEntity<ApiResponse<Void>> response = handler.handleMissingParameter(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ApiResponse<Void> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isSuccess()).isFalse();
        assertThat(body.getError().getCode()).isEqualTo(ErrorCode.MISSING_REQUIRED_PARAMETER.getCode());
        assertThat(body.getError().getMessage()).contains("q");
    }

    @Test
    @DisplayName("HttpMessageNotReadableException → 400 / C007")
    void handleHttpMessageNotReadable() {
        var ex = new HttpMessageNotReadableException("Malformed JSON",
                (org.springframework.http.HttpInputMessage) null);

        ResponseEntity<ApiResponse<Void>> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo(ErrorCode.INVALID_REQUEST_BODY.getCode());
    }

    @Test
    @DisplayName("ConstraintViolationException → 400 / C001, 위반 메시지 포함")
    void handleConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("size");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be greater than 0");

        var ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<ApiResponse<Void>> response = handler.handleConstraintViolation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo(ErrorCode.INVALID_INPUT_VALUE.getCode());
        assertThat(response.getBody().getError().getMessage()).contains("size");
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException → 405 / C003")
    void handleMethodNotSupported() {
        var ex = new HttpRequestMethodNotSupportedException("DELETE");

        ResponseEntity<ApiResponse<Void>> response = handler.handleHttpRequestMethodNotSupported(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo(ErrorCode.METHOD_NOT_ALLOWED.getCode());
    }

    @Test
    @DisplayName("NoResourceFoundException → 404 / C004")
    void handleNoResourceFound() {
        var ex = new NoResourceFoundException(org.springframework.http.HttpMethod.GET, "/missing");

        ResponseEntity<ApiResponse<Void>> response = handler.handleNoResourceFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("BusinessException → 자체 status/code")
    void handleBusinessException() {
        var ex = new BusinessException(ErrorCode.STOCK_NOT_FOUND);

        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo(ErrorCode.STOCK_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("처리되지 않은 Exception → 500 / C002")
    void handleUnhandledException() {
        var ex = new RuntimeException("boom");

        ResponseEntity<ApiResponse<Void>> response = handler.handleException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
    }
}
