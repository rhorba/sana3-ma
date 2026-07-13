package ma.sana3.adapter.persistence.artisanprofile;

import ma.sana3.domain.artisanprofile.CooperativeInvite;

final class CooperativeInviteEntityMapper {

  private CooperativeInviteEntityMapper() {}

  static CooperativeInvite toDomain(CooperativeInviteJpaEntity entity) {
    return new CooperativeInvite(
        entity.getId(),
        entity.getArtisanProfileId(),
        entity.getInvitedUserId(),
        entity.getStatus(),
        entity.getCreatedAt(),
        entity.getResolvedAt());
  }

  static CooperativeInviteJpaEntity toEntity(CooperativeInvite invite) {
    return new CooperativeInviteJpaEntity(
        invite.id(),
        invite.artisanProfileId(),
        invite.invitedUserId(),
        invite.status(),
        invite.createdAt(),
        invite.resolvedAt());
  }
}
