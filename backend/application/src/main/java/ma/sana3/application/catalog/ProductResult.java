package ma.sana3.application.catalog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResult(
    UUID id,
    UUID artisanProfileId,
    String name,
    String description,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType,
    String imageUrl,
    Instant createdAt,
    Instant updatedAt) {}
