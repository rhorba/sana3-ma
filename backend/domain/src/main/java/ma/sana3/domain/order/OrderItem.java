package ma.sana3.domain.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class OrderItem {

  private final UUID id;
  private final UUID orderId;
  private final UUID productId;
  private final String productNameSnapshot;
  private final BigDecimal priceAmountSnapshot;
  private final String priceCurrencySnapshot;
  private final String craftTypeSnapshot;
  private final UUID artisanProfileId;
  private final int quantity;
  private final Instant completedAt;
  private final Instant createdAt;

  public OrderItem(
      UUID id,
      UUID orderId,
      UUID productId,
      String productNameSnapshot,
      BigDecimal priceAmountSnapshot,
      String priceCurrencySnapshot,
      String craftTypeSnapshot,
      UUID artisanProfileId,
      int quantity,
      Instant completedAt,
      Instant createdAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.orderId = Objects.requireNonNull(orderId, "orderId");
    this.productId = productId;
    this.productNameSnapshot = requireNonBlank(productNameSnapshot, "productNameSnapshot");
    this.priceAmountSnapshot = requirePositive(priceAmountSnapshot, "priceAmountSnapshot");
    this.priceCurrencySnapshot = requireNonBlank(priceCurrencySnapshot, "priceCurrencySnapshot");
    this.craftTypeSnapshot = requireNonBlank(craftTypeSnapshot, "craftTypeSnapshot");
    this.artisanProfileId = Objects.requireNonNull(artisanProfileId, "artisanProfileId");
    this.quantity = requirePositiveQuantity(quantity);
    this.completedAt = completedAt;
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
  }

  public static OrderItem create(
      UUID orderId,
      UUID productId,
      String productNameSnapshot,
      BigDecimal priceAmountSnapshot,
      String priceCurrencySnapshot,
      String craftTypeSnapshot,
      UUID artisanProfileId,
      int quantity) {
    return new OrderItem(
        UUID.randomUUID(),
        orderId,
        productId,
        productNameSnapshot,
        priceAmountSnapshot,
        priceCurrencySnapshot,
        craftTypeSnapshot,
        artisanProfileId,
        quantity,
        null,
        Instant.now());
  }

  public OrderItem complete() {
    if (completedAt != null) {
      throw new IllegalStateException("Order item is already completed");
    }
    return new OrderItem(
        id,
        orderId,
        productId,
        productNameSnapshot,
        priceAmountSnapshot,
        priceCurrencySnapshot,
        craftTypeSnapshot,
        artisanProfileId,
        quantity,
        Instant.now(),
        createdAt);
  }

  public boolean isCompleted() {
    return completedAt != null;
  }

  public BigDecimal lineTotal() {
    return priceAmountSnapshot.multiply(BigDecimal.valueOf(quantity));
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

  private static int requirePositiveQuantity(int quantity) {
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity must be positive");
    }
    return quantity;
  }

  public UUID id() {
    return id;
  }

  public UUID orderId() {
    return orderId;
  }

  public UUID productId() {
    return productId;
  }

  public String productNameSnapshot() {
    return productNameSnapshot;
  }

  public BigDecimal priceAmountSnapshot() {
    return priceAmountSnapshot;
  }

  public String priceCurrencySnapshot() {
    return priceCurrencySnapshot;
  }

  public String craftTypeSnapshot() {
    return craftTypeSnapshot;
  }

  public UUID artisanProfileId() {
    return artisanProfileId;
  }

  public int quantity() {
    return quantity;
  }

  public Instant completedAt() {
    return completedAt;
  }

  public Instant createdAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderItem other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
