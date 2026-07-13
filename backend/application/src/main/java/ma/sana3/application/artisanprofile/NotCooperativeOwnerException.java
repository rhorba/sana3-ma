package ma.sana3.application.artisanprofile;

public class NotCooperativeOwnerException extends RuntimeException {

  public NotCooperativeOwnerException() {
    super("Only the cooperative's owner may perform this action");
  }
}
