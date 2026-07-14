package ma.sana3.adapter.web.certification;

import java.time.Instant;
import java.util.UUID;
import ma.sana3.application.certification.CertificateResult;

public record CertificateResponse(UUID id, UUID productId, Instant issuedAt) {
  static CertificateResponse from(CertificateResult result) {
    return new CertificateResponse(result.id(), result.productId(), result.issuedAt());
  }
}
