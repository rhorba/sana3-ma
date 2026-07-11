package ma.sana3.adapter.web.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import ma.sana3.application.order.OrderItemResult;

public record OrderItemResponse(
    UUID id,
    UUID productId,
    String productName,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType,
    UUID artisanProfileId,
    int quantity,
    BigDecimal lineTotal,
    boolean completed,
    Instant completedAt) {
  static OrderItemResponse from(OrderItemResult result) {
    return new OrderItemResponse(
        result.id(),
        result.productId(),
        result.productName(),
        result.priceAmount(),
        result.priceCurrency(),
        result.craftType(),
        result.artisanProfileId(),
        result.quantity(),
        result.lineTotal(),
        result.completed(),
        result.completedAt());
  }
}
