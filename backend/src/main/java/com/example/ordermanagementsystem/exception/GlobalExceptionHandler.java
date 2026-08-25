package com.example.ordermanagementsystem.exception;

import com.example.ordermanagementsystem.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(
            ResourceNotFoundException exception) {

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception exception) {

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            errors.add(fieldError.getDefaultMessage());
        }

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");
        response.setData(errors);   // danh sách lỗi giờ nằm trong "data", giống mọi response khác

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleCategoryAlreadyExistsException(
            CategoryAlreadyExistsException exception) {

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse> handleDuplicateResourceException(DuplicateResourceException exception) {
        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(exception.getMessage());
        response.setData(null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ApiResponse> handleInvalidOrderStatusException(
            InvalidOrderStatusException exception) {

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ApiResponse> handleProductOutOfStockException(
            ProductOutOfStockException exception) {

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(
            AuthenticationException exception) {

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Sai tên đăng nhập hoặc mật khẩu");
        response.setData(null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse> handleRefreshTokenExpiredException(
            InvalidRefreshTokenException exception) {

        ApiResponse response = new ApiResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setMessage(exception.getMessage());
        response.setData(null);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}