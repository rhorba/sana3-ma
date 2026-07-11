package ma.sana3.adapter.persistence.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity {

  @Id private UUID id;

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "product_id")
  private UUID productId;

  @Column(name = "product_name_snapshot", nullable = false)
  private String productNameSnapshot;

  @Column(name = "price_amount_snapshot", nullable = false)
  private BigDecimal priceAmountSnapshot;

  @Column(name = "price_currency_snapshot", nullable = false)
  private String priceCurrencySnapshot;

  @Column(name = "craft_type_snapshot", nullable = false)
  private String craftTypeSnapshot;

  @Column(name = "artisan_profile_id", nullable = false)
  private UUID artisanProfileId;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  @Column(name = "completed_at")
  private Instant completedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected OrderItemJpaEntity() {
    // JPA
  }

  public OrderItemJpaEntity(
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
    this.id = id;
    this.orderId = orderId;
    this.productId = productId;
    this.productNameSnapshot = productNameSnapshot;
    this.priceAmountSnapshot = priceAmountSnapshot;
    this.priceCurrencySnapshot = priceCurrencySnapshot;
    this.craftTypeSnapshot = craftTypeSnapshot;
    this.artisanProfileId = artisanProfileId;
    this.quantity = quantity;
    this.completedAt = completedAt;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getProductId() {
    return productId;
  }

  public String getProductNameSnapshot() {
    return productNameSnapshot;
  }

  public BigDecimal getPriceAmountSnapshot() {
    return priceAmountSnapshot;
  }

  public String getPriceCurrencySnapshot() {
    return priceCurrencySnapshot;
  }

  public String getCraftTypeSnapshot() {
    return craftTypeSnapshot;
  }

  public UUID getArtisanProfileId() {
    return artisanProfileId;
  }

  public int getQuantity() {
    return quantity;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
