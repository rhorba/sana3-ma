package ma.sana3.application.artisanprofile;

import java.time.Instant;
import java.util.UUID;

public record ArtisanProfileResult(
    UUID id,
    String displayName,
    String craftType,
    String region,
    String bio,
    String contactPhone,
    Instant createdAt,
    Instant updatedAt) {}
