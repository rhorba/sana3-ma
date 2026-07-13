package ma.sana3.application.artisanprofile;

public class InviteeNotEligibleException extends RuntimeException {

  public InviteeNotEligibleException() {
    super("No eligible artisan account was found for that email");
  }
}
