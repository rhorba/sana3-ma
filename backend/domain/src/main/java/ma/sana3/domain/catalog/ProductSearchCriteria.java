package ma.sana3.domain.catalog;

import java.math.BigDecimal;

public record ProductSearchCriteria(
    String craftType,
    String region,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    String keyword,
    int page,
    int pageSize) {}
