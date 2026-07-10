package ma.sana3.adapter.web.auth;

import java.util.List;
import ma.sana3.application.auth.InvalidCredentialsException;
import ma.sana3.application.auth.InvalidTokenException;
import ma.sana3.domain.user.DuplicateEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "ma.sana3.adapter.web.auth")
class AuthExceptionHandler {

  @ExceptionHandler(DuplicateEmailException.class)
  ResponseEntity<ApiError> handleDuplicateEmail() {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("EMAIL_ALREADY_REGISTERED", "A user with this email already exists"));
  }

  @ExceptionHandler({InvalidCredentialsException.class, InvalidTokenException.class})
  ResponseEntity<ApiError> handleUnauthorized(RuntimeException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiError("UNAUTHORIZED", exception.getMessage()));
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
