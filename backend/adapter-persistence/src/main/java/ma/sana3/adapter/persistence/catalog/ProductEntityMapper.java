package ma.sana3.adapter.persistence.catalog;

import ma.sana3.domain.catalog.Product;

final class ProductEntityMapper {

  private ProductEntityMapper() {}

  static Product toDomain(ProductJpaEntity entity) {
    return new Product(
        entity.getId(),
        entity.getArtisanProfileId(),
        entity.getName(),
        entity.getDescription(),
        entity.getPriceAmount(),
        entity.getPriceCurrency(),
        entity.getCraftType(),
        entity.getImageUrl(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static ProductJpaEntity toEntity(Product product) {
    return new ProductJpaEntity(
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
