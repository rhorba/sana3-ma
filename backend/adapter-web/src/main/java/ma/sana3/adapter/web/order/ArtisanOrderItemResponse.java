package ma.sana3.adapter.web.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import ma.sana3.application.order.ArtisanOrderItemResult;
import ma.sana3.domain.order.OrderStatus;

public record ArtisanOrderItemResponse(
    UUID id,
    UUID orderId,
    OrderStatus orderStatus,
    String shippingAddress,
    String buyerEmail,
    UUID productId,
    String productName,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType,
    int quantity,
    BigDecimal lineTotal,
    boolean completed,
    Instant completedAt,
    Instant orderCreatedAt) {
  static ArtisanOrderItemResponse from(ArtisanOrderItemResult result) {
    return new ArtisanOrderItemResponse(
        result.id(),
        result.orderId(),
        result.orderStatus(),
        result.shippingAddress(),
        result.buyerEmail(),
        result.productId(),
        result.productName(),
        result.priceAmount(),
        result.priceCurrency(),
        result.craftType(),
        result.quantity(),
        result.lineTotal(),
        result.completed(),
        result.completedAt(),
        result.orderCreatedAt());
  }
}
