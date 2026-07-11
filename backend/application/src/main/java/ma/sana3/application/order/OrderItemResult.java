package ma.sana3.application.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderItemResult(
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
    Instant completedAt) {}
