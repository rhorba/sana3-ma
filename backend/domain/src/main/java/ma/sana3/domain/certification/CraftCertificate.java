package ma.sana3.domain.certification;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class CraftCertificate {

  private final UUID id;
  private final UUID productId;
  private final UUID artisanProfileId;
  private final Instant issuedAt;

  public CraftCertificate(UUID id, UUID productId, UUID artisanProfileId, Instant issuedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.productId = Objects.requireNonNull(productId, "productId");
    this.artisanProfileId = Objects.requireNonNull(artisanProfileId, "artisanProfileId");
    this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt");
  }

  public static CraftCertificate issue(UUID productId, UUID artisanProfileId) {
    return new CraftCertificate(UUID.randomUUID(), productId, artisanProfileId, Instant.now());
  }

  public UUID id() {
    return id;
  }

  public UUID productId() {
    return productId;
  }

  public UUID artisanProfileId() {
    return artisanProfileId;
  }

  public Instant issuedAt() {
    return issuedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CraftCertificate other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
