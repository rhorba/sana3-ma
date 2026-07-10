package ma.sana3.application.catalog;

import java.math.BigDecimal;
import java.util.UUID;
import ma.sana3.domain.user.Role;

public record CreateProductCommand(
    UUID userId,
    Role userRole,
    String name,
    String description,
    BigDecimal priceAmount,
    String priceCurrency,
    String craftType) {}
