package ma.sana3.adapter.web.auth;

import java.util.List;

public record ApiError(ErrorBody error) {

  public ApiError(String code, String message) {
    this(new ErrorBody(code, message, List.of()));
  }

  public ApiError(String code, String message, List<FieldError> details) {
    this(new ErrorBody(code, message, details));
  }

  public record ErrorBody(String code, String message, List<FieldError> details) {}

  public record FieldError(String field, String message) {}
}
