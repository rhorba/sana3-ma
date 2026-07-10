package ma.sana3.application.artisanprofile;

public class ProfileNotFoundException extends RuntimeException {

  public ProfileNotFoundException() {
    super("No artisan profile exists for this user");
  }
}
