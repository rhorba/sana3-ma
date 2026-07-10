package ma.sana3.application.catalog;

import java.math.BigDecimal;
import java.util.UUID;
import ma.sana3.domain.user.Role;

public record UpdateProductCommand(
    UUID userId,
    Role userRole,
    UUID productId,
    String name,
    String description,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType,
    String imageUrl) {}
