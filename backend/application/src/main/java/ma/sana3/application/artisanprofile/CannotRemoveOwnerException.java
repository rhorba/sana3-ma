package ma.sana3.application.artisanprofile;

public class CannotRemoveOwnerException extends RuntimeException {

  public CannotRemoveOwnerException() {
    super("The cooperative's owner cannot be removed");
  }
}
