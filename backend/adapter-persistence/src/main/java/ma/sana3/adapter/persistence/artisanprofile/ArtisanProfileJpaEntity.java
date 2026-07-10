package ma.sana3.adapter.persistence.artisanprofile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "artisan_profiles")
public class ArtisanProfileJpaEntity {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "craft_type", nullable = false)
  private String craftType;

  @Column(name = "region")
  private String region;

  @Column(name = "bio")
  private String bio;

  @Column(name = "contact_phone")
  private String contactPhone;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected ArtisanProfileJpaEntity() {
    // JPA
  }

  public ArtisanProfileJpaEntity(
      UUID id,
      UUID userId,
      String displayName,
      String craftType,
      String region,
      String bio,
      String contactPhone,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.userId = userId;
    this.displayName = displayName;
    this.craftType = craftType;
    this.region = region;
    this.bio = bio;
    this.contactPhone = contactPhone;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getCraftType() {
    return craftType;
  }

  public String getRegion() {
    return region;
  }

  public String getBio() {
    return bio;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
