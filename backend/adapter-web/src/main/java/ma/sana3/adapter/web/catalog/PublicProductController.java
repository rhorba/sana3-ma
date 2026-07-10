package ma.sana3.adapter.web.catalog;

import java.math.BigDecimal;
import java.util.UUID;
import ma.sana3.application.catalog.GetProductDetailHandler;
import ma.sana3.application.catalog.GetProductDetailQuery;
import ma.sana3.application.catalog.SearchProductsHandler;
import ma.sana3.application.catalog.SearchProductsQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
class PublicProductController {

  private final SearchProductsHandler searchProductsHandler;
  private final GetProductDetailHandler getProductDetailHandler;

  PublicProductController(
      SearchProductsHandler searchProductsHandler,
      GetProductDetailHandler getProductDetailHandler) {
    this.searchProductsHandler = searchProductsHandler;
    this.getProductDetailHandler = getProductDetailHandler;
  }

  @GetMapping
  PublicProductPageResponse search(
      @RequestParam(required = false) String craftType,
      @RequestParam(required = false) String region,
      @RequestParam(required = false) BigDecimal minPrice,
      @RequestParam(required = false) BigDecimal maxPrice,
      @RequestParam(required = false) String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int pageSize) {
    return PublicProductPageResponse.from(
        searchProductsHandler.handle(
            new SearchProductsQuery(craftType, region, minPrice, maxPrice, q, page, pageSize)));
  }

  @GetMapping("/{id}")
  PublicProductResponse detail(@PathVariable UUID id) {
    return PublicProductResponse.from(
        getProductDetailHandler.handle(new GetProductDetailQuery(id)));
  }
}
