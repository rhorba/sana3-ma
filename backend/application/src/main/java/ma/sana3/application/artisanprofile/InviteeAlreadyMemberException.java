package ma.sana3.application.artisanprofile;

public class InviteeAlreadyMemberException extends RuntimeException {

  public InviteeAlreadyMemberException() {
    super("That user already belongs to a cooperative");
  }
}
