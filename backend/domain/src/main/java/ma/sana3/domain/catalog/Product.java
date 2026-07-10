package ma.sana3.domain.catalog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Product {

  private final UUID id;
  private final UUID artisanProfileId;
  private final String name;
  private final String description;
  private final BigDecimal priceAmount;
  private final String priceCurrency;
  private final String craftType;
  private final String imageUrl;
  private final Instant createdAt;
  private final Instant updatedAt;

  public Product(
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
    this.id = Objects.requireNonNull(id, "id");
    this.artisanProfileId = Objects.requireNonNull(artisanProfileId, "artisanProfileId");
    this.name = requireNonBlank(name, "name");
    this.description = description;
    this.priceAmount = requirePositive(priceAmount, "priceAmount");
    this.priceCurrency = requireNonBlank(priceCurrency, "priceCurrency");
    this.craftType = requireNonBlank(craftType, "craftType");
    this.imageUrl = imageUrl;
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public static Product create(
      UUID artisanProfileId,
      String name,
      String description,
      BigDecimal priceAmount,
      String priceCurrency,
      String craftType,
      String imageUrl) {
    Instant now = Instant.now();
    return new Product(
        UUID.randomUUID(),
        artisanProfileId,
        name,
        description,
        priceAmount,
        priceCurrency,
        craftType,
        imageUrl,
        now,
        now);
  }

  public Product withDetails(
      String name,
      String description,
      BigDecimal priceAmount,
      String priceCurrency,
      String craftType,
      String imageUrl) {
    return new Product(
        id,
        artisanProfileId,
        name,
        description,
        priceAmount,
        priceCurrency,
        craftType,
        imageUrl,
        createdAt,
        Instant.now());
  }

  private static String requireNonBlank(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
    return value;
  }

  private static BigDecimal requirePositive(BigDecimal value, String field) {
    if (value == null || value.signum() <= 0) {
      throw new IllegalArgumentException(field + " must be positive");
    }
    return value;
  }

  public UUID id() {
    return id;
  }

  public UUID artisanProfileId() {
    return artisanProfileId;
  }

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }

  public BigDecimal priceAmount() {
    return priceAmount;
  }

  public String priceCurrency() {
    return priceCurrency;
  }

  public String craftType() {
    return craftType;
  }

  public String imageUrl() {
    return imageUrl;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
