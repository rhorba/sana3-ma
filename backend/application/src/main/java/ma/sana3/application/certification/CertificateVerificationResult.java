package ma.sana3.application.certification;

import java.time.Instant;

public record CertificateVerificationResult(
    String artisanDisplayName, String productName, String craftType, Instant issuedAt) {}
