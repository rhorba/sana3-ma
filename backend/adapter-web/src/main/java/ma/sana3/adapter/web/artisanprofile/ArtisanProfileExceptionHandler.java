package ma.sana3.adapter.web.artisanprofile;

import ma.sana3.adapter.web.auth.ApiError;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice(basePackages = "ma.sana3.adapter.web.artisanprofile")
class ArtisanProfileExceptionHandler {

    @ExceptionHandler(NotAnArtisanException.class)
    ResponseEntity<ApiError> handleNotAnArtisan(NotAnArtisanException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiError("NOT_AN_ARTISAN", exception.getMessage()));
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    ResponseEntity<ApiError> handleProfileNotFound(ProfileNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("PROFILE_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        List<ApiError.FieldError> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ApiError.FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("VALIDATION_FAILED", "The request contains invalid fields", details));
    }
}
