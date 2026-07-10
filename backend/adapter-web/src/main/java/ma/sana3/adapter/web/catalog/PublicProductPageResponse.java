package ma.sana3.adapter.web.catalog;

import java.util.List;
import ma.sana3.application.catalog.PublicProductPage;

public record PublicProductPageResponse(
    List<PublicProductResponse> products, long totalElements, int page, int pageSize) {
  static PublicProductPageResponse from(PublicProductPage page) {
    return new PublicProductPageResponse(
        page.products().stream().map(PublicProductResponse::from).toList(),
        page.totalElements(),
        page.page(),
        page.pageSize());
  }
}
