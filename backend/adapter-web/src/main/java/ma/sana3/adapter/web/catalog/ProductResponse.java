package ma.sana3.adapter.web.catalog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import ma.sana3.application.catalog.ProductResult;

public record ProductResponse(
    UUID id,
    UUID artisanProfileId,
    String name,
    String description,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType,
    String imageUrl,
    Instant createdAt,
    Instant updatedAt) {
  static ProductResponse from(ProductResult result) {
    return new ProductResponse(
        result.id(),
        result.artisanProfileId(),
        result.name(),
        result.description(),
        result.priceAmount(),
        result.priceCurrency(),
        result.craftType(),
        result.imageUrl(),
        result.createdAt(),
        result.updatedAt());
  }
}
