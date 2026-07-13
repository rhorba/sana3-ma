package ma.sana3.domain.artisanprofile;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CooperativeInvite {

  private final UUID id;
  private final UUID artisanProfileId;
  private final UUID invitedUserId;
  private final InviteStatus status;
  private final Instant createdAt;
  private final Instant resolvedAt;

  public CooperativeInvite(
      UUID id,
      UUID artisanProfileId,
      UUID invitedUserId,
      InviteStatus status,
      Instant createdAt,
      Instant resolvedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.artisanProfileId = Objects.requireNonNull(artisanProfileId, "artisanProfileId");
    this.invitedUserId = Objects.requireNonNull(invitedUserId, "invitedUserId");
    this.status = Objects.requireNonNull(status, "status");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.resolvedAt = resolvedAt;
  }

  public static CooperativeInvite create(UUID artisanProfileId, UUID invitedUserId) {
    return new CooperativeInvite(
        UUID.randomUUID(),
        artisanProfileId,
        invitedUserId,
        InviteStatus.PENDING,
        Instant.now(),
        null);
  }

  public CooperativeInvite accept() {
    if (status != InviteStatus.PENDING) {
      throw new IllegalInviteStatusTransitionException(status, InviteStatus.ACCEPTED);
    }
    return new CooperativeInvite(
        id, artisanProfileId, invitedUserId, InviteStatus.ACCEPTED, createdAt, Instant.now());
  }

  public CooperativeInvite decline() {
    if (status != InviteStatus.PENDING) {
      throw new IllegalInviteStatusTransitionException(status, InviteStatus.DECLINED);
    }
    return new CooperativeInvite(
        id, artisanProfileId, invitedUserId, InviteStatus.DECLINED, createdAt, Instant.now());
  }

  public UUID id() {
    return id;
  }

  public UUID artisanProfileId() {
    return artisanProfileId;
  }

  public UUID invitedUserId() {
    return invitedUserId;
  }

  public InviteStatus status() {
    return status;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant resolvedAt() {
    return resolvedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CooperativeInvite other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
