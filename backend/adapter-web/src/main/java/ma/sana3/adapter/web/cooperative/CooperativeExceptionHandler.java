package ma.sana3.adapter.web.cooperative;

import java.util.List;
import ma.sana3.adapter.web.auth.ApiError;
import ma.sana3.application.artisanprofile.CannotRemoveOwnerException;
import ma.sana3.application.artisanprofile.InviteNotFoundException;
import ma.sana3.application.artisanprofile.InviteeAlreadyInvitedException;
import ma.sana3.application.artisanprofile.InviteeAlreadyMemberException;
import ma.sana3.application.artisanprofile.InviteeNotEligibleException;
import ma.sana3.application.artisanprofile.MemberNotFoundException;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.NotCooperativeOwnerException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.IllegalInviteStatusTransitionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "ma.sana3.adapter.web.cooperative")
class CooperativeExceptionHandler {

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

  @ExceptionHandler(NotCooperativeOwnerException.class)
  ResponseEntity<ApiError> handleNotCooperativeOwner(NotCooperativeOwnerException exception) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ApiError("NOT_COOPERATIVE_OWNER", exception.getMessage()));
  }

  @ExceptionHandler(InviteeNotEligibleException.class)
  ResponseEntity<ApiError> handleInviteeNotEligible(InviteeNotEligibleException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError("INVITEE_NOT_ELIGIBLE", exception.getMessage()));
  }

  @ExceptionHandler(InviteeAlreadyMemberException.class)
  ResponseEntity<ApiError> handleInviteeAlreadyMember(InviteeAlreadyMemberException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("INVITEE_ALREADY_MEMBER", exception.getMessage()));
  }

  @ExceptionHandler(InviteeAlreadyInvitedException.class)
  ResponseEntity<ApiError> handleInviteeAlreadyInvited(InviteeAlreadyInvitedException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("INVITEE_ALREADY_INVITED", exception.getMessage()));
  }

  @ExceptionHandler(InviteNotFoundException.class)
  ResponseEntity<ApiError> handleInviteNotFound(InviteNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError("INVITE_NOT_FOUND", exception.getMessage()));
  }

  @ExceptionHandler(IllegalInviteStatusTransitionException.class)
  ResponseEntity<ApiError> handleIllegalInviteStatusTransition(
      IllegalInviteStatusTransitionException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("ILLEGAL_INVITE_STATUS_TRANSITION", exception.getMessage()));
  }

  @ExceptionHandler(CannotRemoveOwnerException.class)
  ResponseEntity<ApiError> handleCannotRemoveOwner(CannotRemoveOwnerException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("CANNOT_REMOVE_OWNER", exception.getMessage()));
  }

  @ExceptionHandler(MemberNotFoundException.class)
  ResponseEntity<ApiError> handleMemberNotFound(MemberNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiError("MEMBER_NOT_FOUND", exception.getMessage()));
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
