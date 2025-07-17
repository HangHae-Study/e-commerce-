package kr.hhplus.be.server.common;

import jakarta.validation.ConstraintViolationException;
import kr.hhplus.be.server.common.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatusException(ResponseStatusException ex) {
        String errorCode = ex.getReason();
        String message   = ex.getReason();
        ApiResponse<Void> body = new ApiResponse<>(errorCode, message, null);
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    // @RequestBody 검증 실패 (DTO의 @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        // 모든 필드 에러 메시지를 하나의 문자열로 합칩니다.
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        ApiResponse<Void> body = new ApiResponse<>("INVALID_REQUEST", errors, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // @PathVariable, @RequestParam
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.joining("; "));
        ApiResponse<Void> body = new ApiResponse<>("INVALID_REQUEST", errors, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
