package ma.sana3.domain.artisanprofile;

public class IllegalInviteStatusTransitionException extends RuntimeException {

  public IllegalInviteStatusTransitionException(InviteStatus from, InviteStatus to) {
    super("Cannot transition invite from " + from + " to " + to);
  }
}
