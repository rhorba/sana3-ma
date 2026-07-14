package ma.sana3.adapter.web.certification;

import java.time.Instant;
import ma.sana3.application.certification.CertificateVerificationResult;

public record CertificateVerificationResponse(
    String artisanDisplayName, String productName, String craftType, Instant issuedAt) {
  static CertificateVerificationResponse from(CertificateVerificationResult result) {
    return new CertificateVerificationResponse(
        result.artisanDisplayName(), result.productName(), result.craftType(), result.issuedAt());
  }
}
