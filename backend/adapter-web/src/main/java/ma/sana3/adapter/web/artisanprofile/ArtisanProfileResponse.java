package ma.sana3.adapter.web.artisanprofile;

import ma.sana3.application.artisanprofile.ArtisanProfileResult;

import java.time.Instant;
import java.util.UUID;

public record ArtisanProfileResponse(
        UUID id,
        UUID userId,
        String displayName,
        String craftType,
        String region,
        String bio,
        String contactPhone,
        Instant createdAt,
        Instant updatedAt
) {
    static ArtisanProfileResponse from(ArtisanProfileResult result) {
        return new ArtisanProfileResponse(
                result.id(),
                result.userId(),
                result.displayName(),
                result.craftType(),
                result.region(),
                result.bio(),
                result.contactPhone(),
                result.createdAt(),
                result.updatedAt());
    }
}
