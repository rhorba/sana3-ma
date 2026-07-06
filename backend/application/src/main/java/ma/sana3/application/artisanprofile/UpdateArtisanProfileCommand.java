package ma.sana3.application.artisanprofile;

import ma.sana3.domain.user.Role;

import java.util.UUID;

public record UpdateArtisanProfileCommand(
        UUID userId,
        Role userRole,
        String displayName,
        String craftType,
        String region,
        String bio,
        String contactPhone
) {
}
