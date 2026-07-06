package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfile;

final class ArtisanProfileResultMapper {

    private ArtisanProfileResultMapper() {
    }

    static ArtisanProfileResult toResult(ArtisanProfile profile) {
        return new ArtisanProfileResult(
                profile.id(),
                profile.userId(),
                profile.displayName(),
                profile.craftType(),
                profile.region(),
                profile.bio(),
                profile.contactPhone(),
                profile.createdAt(),
                profile.updatedAt());
    }
}
