package ma.sana3.adapter.web.certification;

import ma.sana3.adapter.web.auth.ApiError;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.application.certification.CertificateNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "ma.sana3.adapter.web.certification")
class CertificateExceptionHandler {

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

  @ExceptionHandler(ProductNotFoundException.class)
  ResponseEntity<ApiError> handleProductNotFound(ProductNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError("PRODUCT_NOT_FOUND", exception.getMessage()));
  }

  @ExceptionHandler(CertificateNotFoundException.class)
  ResponseEntity<ApiError> handleCertificateNotFound(CertificateNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError("CERTIFICATE_NOT_FOUND", exception.getMessage()));
  }
}
