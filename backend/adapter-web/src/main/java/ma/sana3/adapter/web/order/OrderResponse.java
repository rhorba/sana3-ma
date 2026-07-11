package ma.sana3.adapter.web.order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.order.OrderResult;
import ma.sana3.domain.order.OrderStatus;

public record OrderResponse(
    UUID id,
    UUID buyerUserId,
    OrderStatus status,
    String shippingAddress,
    List<OrderItemResponse> items,
    List<OrderTotalResponse> totals,
    Instant createdAt,
    Instant updatedAt) {
  static OrderResponse from(OrderResult result) {
    return new OrderResponse(
        result.id(),
        result.buyerUserId(),
        result.status(),
        result.shippingAddress(),
        result.items().stream().map(OrderItemResponse::from).toList(),
        result.totals().stream().map(OrderTotalResponse::from).toList(),
        result.createdAt(),
        result.updatedAt());
  }
}
