package ma.sana3.domain.artisanprofile;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CooperativeMembership {

  private final UUID id;
  private final UUID userId;
  private final UUID artisanProfileId;
  private final MembershipRole role;
  private final Instant joinedAt;

  public CooperativeMembership(
      UUID id, UUID userId, UUID artisanProfileId, MembershipRole role, Instant joinedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.userId = Objects.requireNonNull(userId, "userId");
    this.artisanProfileId = Objects.requireNonNull(artisanProfileId, "artisanProfileId");
    this.role = Objects.requireNonNull(role, "role");
    this.joinedAt = Objects.requireNonNull(joinedAt, "joinedAt");
  }

  public static CooperativeMembership create(
      UUID userId, UUID artisanProfileId, MembershipRole role) {
    return new CooperativeMembership(
        UUID.randomUUID(), userId, artisanProfileId, role, Instant.now());
  }

  public UUID id() {
    return id;
  }

  public UUID userId() {
    return userId;
  }

  public UUID artisanProfileId() {
    return artisanProfileId;
  }

  public MembershipRole role() {
    return role;
  }

  public Instant joinedAt() {
    return joinedAt;
  }

  public boolean isOwner() {
    return role == MembershipRole.OWNER;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CooperativeMembership other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
