package ma.sana3.adapter.web.order;

import java.util.List;
import ma.sana3.adapter.web.auth.ApiError;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.application.order.OrderCancelledException;
import ma.sana3.application.order.OrderHasCompletedItemsException;
import ma.sana3.application.order.OrderItemNotFoundException;
import ma.sana3.application.order.OrderNotFoundException;
import ma.sana3.domain.order.IllegalOrderStatusTransitionException;
import ma.sana3.domain.order.OrderItemAlreadyCompletedException;
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

  @ExceptionHandler(OrderNotFoundException.class)
  ResponseEntity<ApiError> handleOrderNotFound(OrderNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError("ORDER_NOT_FOUND", exception.getMessage()));
  }

  @ExceptionHandler(OrderItemNotFoundException.class)
  ResponseEntity<ApiError> handleOrderItemNotFound(OrderItemNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError("ORDER_ITEM_NOT_FOUND", exception.getMessage()));
  }

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

  @ExceptionHandler(IllegalOrderStatusTransitionException.class)
  ResponseEntity<ApiError> handleIllegalOrderStatusTransition(
      IllegalOrderStatusTransitionException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("ILLEGAL_ORDER_STATUS_TRANSITION", exception.getMessage()));
  }

  @ExceptionHandler(OrderItemAlreadyCompletedException.class)
  ResponseEntity<ApiError> handleOrderItemAlreadyCompleted(
      OrderItemAlreadyCompletedException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("ORDER_ITEM_ALREADY_COMPLETED", exception.getMessage()));
  }

  @ExceptionHandler(OrderHasCompletedItemsException.class)
  ResponseEntity<ApiError> handleOrderHasCompletedItems(OrderHasCompletedItemsException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("ORDER_HAS_COMPLETED_ITEMS", exception.getMessage()));
  }

  @ExceptionHandler(OrderCancelledException.class)
  ResponseEntity<ApiError> handleOrderCancelled(OrderCancelledException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("ORDER_CANCELLED", exception.getMessage()));
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
