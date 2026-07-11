package ma.sana3.application.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import ma.sana3.domain.order.OrderStatus;

public record ArtisanOrderItemResult(
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
    Instant orderCreatedAt) {}
