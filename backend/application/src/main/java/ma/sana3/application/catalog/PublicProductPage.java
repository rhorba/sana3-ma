package ma.sana3.application.catalog;

import java.util.List;

public record PublicProductPage(
    List<PublicProductSummary> products, long totalElements, int page, int pageSize) {}
