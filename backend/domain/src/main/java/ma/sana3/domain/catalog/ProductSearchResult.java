package ma.sana3.domain.catalog;

import java.util.List;

public record ProductSearchResult(
    List<Product> products, long totalElements, int page, int pageSize) {}
