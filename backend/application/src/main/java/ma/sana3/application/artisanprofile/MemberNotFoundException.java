package ma.sana3.application.artisanprofile;

public class MemberNotFoundException extends RuntimeException {

  public MemberNotFoundException() {
    super("No such member of this cooperative");
  }
}
