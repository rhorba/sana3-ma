package ma.sana3.adapter.persistence.artisanprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.InviteStatus;

@Entity
@Table(name = "cooperative_invites")
public class CooperativeInviteJpaEntity {

  @Id private UUID id;

  @Column(name = "artisan_profile_id", nullable = false)
  private UUID artisanProfileId;

  @Column(name = "invited_user_id", nullable = false)
  private UUID invitedUserId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private InviteStatus status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "resolved_at")
  private Instant resolvedAt;

  protected CooperativeInviteJpaEntity() {
    // JPA
  }

  public CooperativeInviteJpaEntity(
      UUID id,
      UUID artisanProfileId,
      UUID invitedUserId,
      InviteStatus status,
      Instant createdAt,
      Instant resolvedAt) {
    this.id = id;
    this.artisanProfileId = artisanProfileId;
    this.invitedUserId = invitedUserId;
    this.status = status;
    this.createdAt = createdAt;
    this.resolvedAt = resolvedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getArtisanProfileId() {
    return artisanProfileId;
  }

  public UUID getInvitedUserId() {
    return invitedUserId;
  }

  public InviteStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getResolvedAt() {
    return resolvedAt;
  }
}
