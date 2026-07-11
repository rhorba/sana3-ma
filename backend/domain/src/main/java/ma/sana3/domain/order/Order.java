package ma.sana3.domain.order;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Order {

  private final UUID id;
  private final UUID buyerUserId;
  private final OrderStatus status;
  private final String shippingAddress;
  private final Instant createdAt;
  private final Instant updatedAt;

  public Order(
      UUID id,
      UUID buyerUserId,
      OrderStatus status,
      String shippingAddress,
      Instant createdAt,
      Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.buyerUserId = Objects.requireNonNull(buyerUserId, "buyerUserId");
    this.status = Objects.requireNonNull(status, "status");
    this.shippingAddress = requireNonBlank(shippingAddress, "shippingAddress");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public static Order place(UUID buyerUserId, String shippingAddress) {
    Instant now = Instant.now();
    return new Order(UUID.randomUUID(), buyerUserId, OrderStatus.PLACED, shippingAddress, now, now);
  }

  public Order cancel() {
    if (status != OrderStatus.PLACED) {
      throw new IllegalOrderStatusTransitionException(status, OrderStatus.CANCELLED);
    }
    return new Order(
        id, buyerUserId, OrderStatus.CANCELLED, shippingAddress, createdAt, Instant.now());
  }

  public Order complete() {
    if (status != OrderStatus.PLACED) {
      throw new IllegalOrderStatusTransitionException(status, OrderStatus.COMPLETED);
    }
    return new Order(
        id, buyerUserId, OrderStatus.COMPLETED, shippingAddress, createdAt, Instant.now());
  }

  private static String requireNonBlank(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
    return value;
  }

  public UUID id() {
    return id;
  }

  public UUID buyerUserId() {
    return buyerUserId;
  }

  public OrderStatus status() {
    return status;
  }

  public String shippingAddress() {
    return shippingAddress;
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
    if (!(o instanceof Order other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
