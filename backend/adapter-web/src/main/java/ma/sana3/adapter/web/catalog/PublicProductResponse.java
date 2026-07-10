package ma.sana3.adapter.web.catalog;

import java.math.BigDecimal;
import java.util.UUID;
import ma.sana3.application.catalog.PublicProductSummary;

public record PublicProductResponse(
    UUID id,
    String name,
    String description,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType,
    String imageUrl,
    PublicArtisanSummaryResponse artisan) {
  static PublicProductResponse from(PublicProductSummary summary) {
    return new PublicProductResponse(
        summary.id(),
        summary.name(),
        summary.description(),
        summary.priceAmount(),
        summary.priceCurrency(),
        summary.craftType(),
        summary.imageUrl(),
        new PublicArtisanSummaryResponse(
            summary.artisanDisplayName(), summary.artisanCraftType(), summary.artisanRegion()));
  }
}
