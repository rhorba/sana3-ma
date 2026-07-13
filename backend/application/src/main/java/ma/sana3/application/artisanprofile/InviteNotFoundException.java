package ma.sana3.application.artisanprofile;

public class InviteNotFoundException extends RuntimeException {

  public InviteNotFoundException() {
    super("No such pending invite for this user");
  }
}
