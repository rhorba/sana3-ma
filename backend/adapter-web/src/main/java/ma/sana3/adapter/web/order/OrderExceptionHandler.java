package ma.sana3.adapter.web.order;

import java.util.List;
import ma.sana3.adapter.web.auth.ApiError;
import ma.sana3.application.catalog.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "ma.sana3.adapter.web.order")
class OrderExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError("PRODUCT_NOT_FOUND", exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
    List<ApiError.FieldError> details =
        exception.getBindingResult().getFieldErrors().stream()
            .map(
                fieldError ->
                    new ApiError.FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
            .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError("VALIDATION_FAILED", "The request contains invalid fields", details));
  }
}
