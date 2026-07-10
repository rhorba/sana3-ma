package ma.sana3.application.catalog;

import ma.sana3.domain.catalog.Product;

final class ProductResultMapper {

  private ProductResultMapper() {}

  static ProductResult toResult(Product product) {
    return new ProductResult(
        product.id(),
        product.artisanProfileId(),
        product.name(),
        product.description(),
        product.priceAmount(),
        product.priceCurrency(),
        product.craftType(),
        product.imageUrl(),
        product.createdAt(),
        product.updatedAt());
  }
}
