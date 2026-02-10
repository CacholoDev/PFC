package com.pfcdaw.pfcdaw.config;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
