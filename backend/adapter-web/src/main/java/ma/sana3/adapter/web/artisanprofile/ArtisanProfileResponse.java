package ma.sana3.adapter.web.artisanprofile;

import java.time.Instant;
import java.util.UUID;
import ma.sana3.application.artisanprofile.ArtisanProfileResult;

public record ArtisanProfileResponse(
    UUID id,
    String displayName,
    String craftType,
    String region,
    String bio,
    String contactPhone,
    Instant createdAt,
    Instant updatedAt) {
  static ArtisanProfileResponse from(ArtisanProfileResult result) {
    return new ArtisanProfileResponse(
        result.id(),
        result.displayName(),
        result.craftType(),
        result.region(),
        result.bio(),
        result.contactPhone(),
        result.createdAt(),
        result.updatedAt());
  }
}
