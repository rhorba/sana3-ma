package ma.sana3.application.certification;

import java.time.Instant;
import java.util.UUID;

public record CertificateResult(UUID id, UUID productId, Instant issuedAt) {}
