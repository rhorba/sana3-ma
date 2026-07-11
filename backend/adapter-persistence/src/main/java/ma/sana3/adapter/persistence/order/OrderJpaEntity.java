package ma.sana3.adapter.persistence.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

  @Id private UUID id;

  @Column(name = "buyer_user_id", nullable = false)
  private UUID buyerUserId;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "shipping_address", nullable = false)
  private String shippingAddress;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  protected OrderJpaEntity() {
    // JPA
  }

  public OrderJpaEntity(
      UUID id,
      UUID buyerUserId,
      String status,
      String shippingAddress,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id;
    this.buyerUserId = buyerUserId;
    this.status = status;
    this.shippingAddress = shippingAddress;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getBuyerUserId() {
    return buyerUserId;
  }

  public String getStatus() {
    return status;
  }

  public String getShippingAddress() {
    return shippingAddress;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
