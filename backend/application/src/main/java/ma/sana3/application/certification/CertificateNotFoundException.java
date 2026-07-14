package ma.sana3.application.certification;

public class CertificateNotFoundException extends RuntimeException {

  public CertificateNotFoundException() {
    super("No certificate found for that verification code");
  }
}
