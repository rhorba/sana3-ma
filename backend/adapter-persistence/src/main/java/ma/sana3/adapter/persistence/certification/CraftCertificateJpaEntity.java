package ma.sana3.adapter.persistence.certification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "craft_certificates")
public class CraftCertificateJpaEntity {

  @Id private UUID id;

  @Column(name = "product_id", nullable = false, unique = true)
  private UUID productId;

  @Column(name = "artisan_profile_id", nullable = false)
  private UUID artisanProfileId;

  @Column(name = "issued_at", nullable = false)
  private Instant issuedAt;

  protected CraftCertificateJpaEntity() {
    // JPA
  }

  public CraftCertificateJpaEntity(
      UUID id, UUID productId, UUID artisanProfileId, Instant issuedAt) {
    this.id = id;
    this.productId = productId;
    this.artisanProfileId = artisanProfileId;
    this.issuedAt = issuedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getProductId() {
    return productId;
  }

  public UUID getArtisanProfileId() {
    return artisanProfileId;
  }

  public Instant getIssuedAt() {
    return issuedAt;
  }
}
