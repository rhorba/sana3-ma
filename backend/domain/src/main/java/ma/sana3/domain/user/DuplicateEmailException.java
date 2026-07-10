package ma.sana3.domain.user;

public class DuplicateEmailException extends RuntimeException {

  public DuplicateEmailException(String email) {
    super("A user with email '" + email + "' already exists");
  }
}
