package ma.sana3.application.order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.domain.order.OrderStatus;

public record OrderResult(
    UUID id,
    UUID buyerUserId,
    OrderStatus status,
    String shippingAddress,
    List<OrderItemResult> items,
    List<OrderTotal> totals,
    Instant createdAt,
    Instant updatedAt) {}
