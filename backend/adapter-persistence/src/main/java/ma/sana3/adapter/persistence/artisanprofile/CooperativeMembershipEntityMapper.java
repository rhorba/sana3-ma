package ma.sana3.adapter.persistence.artisanprofile;

import ma.sana3.domain.artisanprofile.CooperativeMembership;

final class CooperativeMembershipEntityMapper {

  private CooperativeMembershipEntityMapper() {}

  static CooperativeMembership toDomain(CooperativeMembershipJpaEntity entity) {
    return new CooperativeMembership(
        entity.getId(),
        entity.getUserId(),
        entity.getArtisanProfileId(),
        entity.getRole(),
        entity.getJoinedAt());
  }

  static CooperativeMembershipJpaEntity toEntity(CooperativeMembership membership) {
    return new CooperativeMembershipJpaEntity(
        membership.id(),
        membership.userId(),
        membership.artisanProfileId(),
        membership.role(),
        membership.joinedAt());
  }
}
