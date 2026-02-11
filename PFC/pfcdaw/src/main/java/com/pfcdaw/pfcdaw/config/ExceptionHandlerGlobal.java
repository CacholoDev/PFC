package com.pfcdaw.pfcdaw.config;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.pfcdaw.pfcdaw.dto.GlobalExceptionHandlerDTO;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionHandlerGlobal {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    // Example handler: uses status/message from ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<GlobalExceptionHandlerDTO> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());

        GlobalExceptionHandlerDTO body = GlobalExceptionHandlerDTO.builder()
                .timeStamp(OffsetDateTime.now().format(formatter))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getReason())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // Handler for validation errors @valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalExceptionHandlerDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed");

        GlobalExceptionHandlerDTO body = GlobalExceptionHandlerDTO.builder()
                .timeStamp(OffsetDateTime.now().format(formatter))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(errorMessage)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // excep personalizada
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalExceptionHandlerDTO> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        
                GlobalExceptionHandlerDTO body = GlobalExceptionHandlerDTO.builder()
                        .timeStamp(OffsetDateTime.now().format(formatter))
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message("An unexpected error occurred: " + ex.getMessage())
                        .path(request.getRequestURI())
                        .build();
        
                return ResponseEntity.status(status).body(body);
    }

    // excep personalizada para acceso no autentificado (401)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GlobalExceptionHandlerDTO> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        GlobalExceptionHandlerDTO body = GlobalExceptionHandlerDTO.builder()
                .timeStamp(OffsetDateTime.now().format(formatter))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Authentication failed: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // excep personalizada para acceso denegado (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalExceptionHandlerDTO> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.FORBIDDEN;

        GlobalExceptionHandlerDTO body = GlobalExceptionHandlerDTO.builder()
                .timeStamp(OffsetDateTime.now().format(formatter))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Access denied: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    //json invalido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GlobalExceptionHandlerDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        GlobalExceptionHandlerDTO body = GlobalExceptionHandlerDTO.builder()
                .timeStamp(OffsetDateTime.now().format(formatter))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Invalid JSON: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    // Negocio ( excepciones desde services)
    
}       