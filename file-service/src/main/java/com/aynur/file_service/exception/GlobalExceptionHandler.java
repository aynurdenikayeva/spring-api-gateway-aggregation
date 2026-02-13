package com.aynur.file_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> badRequest(BadRequestException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> forbidden(ForbiddenException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiError> storage(StorageException ex, HttpServletRequest req) {
        // Bu sənin storage layer-dən gələn səhvlər üçündür (disk write/read və s.)
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> any(Exception ex, HttpServletRequest req) {
        // DEBUG üçün: real stacktrace görünsün
        ex.printStackTrace();
        // Client-ə də real səbəbi göstər (debug zamanı rahatdır)
        String msg = ex.getClass().getSimpleName() + ": " + (ex.getMessage() == null ? "No message" : ex.getMessage());
        return build(HttpStatus.INTERNAL_SERVER_ERROR, msg, req.getRequestURI());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String msg, String path) {
        ApiError body = new ApiError(status.value(), status.getReasonPhrase(), msg, path);
        return ResponseEntity.status(status).body(body);
    }
}
