package ma.sana3.adapter.persistence.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductJpaEntity {

  @Id private UUID id;

  @Column(name = "artisan_profile_id", nullable = false)
  private UUID artisanProfileId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "price_amount", nullable = false)
  private BigDecimal priceAmount;

  @Column(name = "price_currency", nullable = false)
  private String priceCurrency;

  @Column(name = "craft_type", nullable = false)
  private String craftType;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected ProductJpaEntity() {
    // JPA
  }

  public ProductJpaEntity(
      UUID id,
      UUID artisanProfileId,
      String name,
      String description,
      BigDecimal priceAmount,
      String priceCurrency,
      String craftType,
      String imageUrl,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.artisanProfileId = artisanProfileId;
    this.name = name;
    this.description = description;
    this.priceAmount = priceAmount;
    this.priceCurrency = priceCurrency;
    this.craftType = craftType;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getArtisanProfileId() {
    return artisanProfileId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPriceAmount() {
    return priceAmount;
  }

  public String getPriceCurrency() {
    return priceCurrency;
  }

  public String getCraftType() {
    return craftType;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
