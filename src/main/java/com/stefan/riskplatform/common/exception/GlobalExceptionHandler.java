package com.stefan.riskplatform.common.exception;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.stefan.riskplatform.common.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Resource not found. path={}, message={}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TenantAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleTenantAccess(
            TenantAccessException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .details(List.of())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidPayloadException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPayload(
            InvalidPayloadException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid payload. path={}, message={}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                List.of()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        log.warn("Validation failed. path={}, details={}", request.getRequestURI(), details);

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String message = "Invalid value for parameter: " + ex.getName();

        String detail = "Parameter '" + ex.getName()
                + "' received invalid value '" + ex.getValue() + "'";

        log.warn("Request parameter type mismatch. path={}, detail={}", request.getRequestURI(), detail);

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request,
                List.of(detail)
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .details(List.of())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        log.warn("Duplicate resource. path={}, message={}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidRuleDefinitionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRuleDefinition(
            InvalidRuleDefinitionException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid rule definition. path={}, message={}", request.getRequestURI(), ex.getMessage());

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                List.of()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error. path={}", request.getRequestURI(), ex);

        ApiErrorResponse response = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected internal server error",
                request,
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private ApiErrorResponse buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> details
    ) {
        return ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .details(details)
                .build();
    }
}