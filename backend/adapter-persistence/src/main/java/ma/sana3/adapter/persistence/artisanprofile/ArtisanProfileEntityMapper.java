package ma.sana3.adapter.persistence.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfile;

final class ArtisanProfileEntityMapper {

  private ArtisanProfileEntityMapper() {}

  static ArtisanProfile toDomain(ArtisanProfileJpaEntity entity) {
    return new ArtisanProfile(
        entity.getId(),
        entity.getDisplayName(),
        entity.getCraftType(),
        entity.getRegion(),
        entity.getBio(),
        entity.getContactPhone(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static ArtisanProfileJpaEntity toEntity(ArtisanProfile profile) {
    return new ArtisanProfileJpaEntity(
        profile.id(),
        profile.displayName(),
        profile.craftType(),
        profile.region(),
        profile.bio(),
        profile.contactPhone(),
        profile.createdAt(),
        profile.updatedAt());
  }
}
