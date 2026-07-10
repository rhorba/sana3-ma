package ma.sana3.application.artisanprofile;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record UpdateArtisanProfileCommand(
    UUID userId,
    Role userRole,
    String displayName,
    String craftType,
    String region,
    String bio,
    String contactPhone) {}
