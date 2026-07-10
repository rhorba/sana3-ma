package ma.sana3.application.catalog;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicProductSummary(
    UUID id,
    UUID artisanProfileId,
    String name,
    String description,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType,
    String imageUrl,
    String artisanDisplayName,
    String artisanCraftType,
    String artisanRegion) {}
