package ma.sana3.adapter.persistence.artisanprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.MembershipRole;

@Entity
@Table(name = "cooperative_members")
public class CooperativeMembershipJpaEntity {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Column(name = "artisan_profile_id", nullable = false)
  private UUID artisanProfileId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private MembershipRole role;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt;

  protected CooperativeMembershipJpaEntity() {
    // JPA
  }

  public CooperativeMembershipJpaEntity(
      UUID id, UUID userId, UUID artisanProfileId, MembershipRole role, Instant joinedAt) {
    this.id = id;
    this.userId = userId;
    this.artisanProfileId = artisanProfileId;
    this.role = role;
    this.joinedAt = joinedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getArtisanProfileId() {
    return artisanProfileId;
  }

  public MembershipRole getRole() {
    return role;
  }

  public Instant getJoinedAt() {
    return joinedAt;
  }
}
