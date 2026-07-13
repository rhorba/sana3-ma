package ma.sana3.application.artisanprofile;

public class InviteeAlreadyInvitedException extends RuntimeException {

  public InviteeAlreadyInvitedException() {
    super("That user already has a pending cooperative invite");
  }
}
