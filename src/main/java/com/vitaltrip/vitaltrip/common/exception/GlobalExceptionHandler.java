package com.vitaltrip.vitaltrip.common.exception;

import com.vitaltrip.vitaltrip.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        log.warn("Custom Exception: {}", e.getMessage());
        ErrorType errorType = e.getErrorType();

        return ResponseEntity
            .status(errorType.getStatus())
            .body(ApiResponse.error(e.getMessage(), errorType.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
        MethodArgumentNotValidException e) {
        log.warn("Validation Exception: {}", e.getMessage());

        return ResponseEntity
            .status(ErrorType.VALIDATION_FAILED.getStatus())
            .body(ApiResponse.error(
                ErrorType.VALIDATION_FAILED.getMessage(),
                ErrorType.VALIDATION_FAILED.getCode()
            ));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleHandlerMethodValidationException(
        HandlerMethodValidationException e) {
        log.warn("Handler Method Validation Exception: {}", e.getMessage());

        String errorMessage = e.getParameterValidationResults().stream()
            .flatMap(result -> result.getResolvableErrors().stream())
            .findFirst()
            .map(MessageSourceResolvable::getDefaultMessage)
            .orElse(ErrorType.VALIDATION_FAILED.getMessage());

        return ResponseEntity
            .status(ErrorType.VALIDATION_FAILED.getStatus())
            .body(ApiResponse.error(errorMessage, ErrorType.VALIDATION_FAILED.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);

        return ResponseEntity
            .status(ErrorType.INTERNAL_SERVER_ERROR.getStatus())
            .body(ApiResponse.error(
                ErrorType.INTERNAL_SERVER_ERROR.getMessage(),
                ErrorType.INTERNAL_SERVER_ERROR.getCode()
            ));
    }
}
