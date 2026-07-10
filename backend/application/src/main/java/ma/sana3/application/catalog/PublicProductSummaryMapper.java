package ma.sana3.application.catalog;

import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.catalog.Product;

final class PublicProductSummaryMapper {

  private PublicProductSummaryMapper() {}

  static PublicProductSummary toSummary(Product product, ArtisanProfile artisan) {
    return new PublicProductSummary(
        product.id(),
        product.artisanProfileId(),
        product.name(),
        product.description(),
        product.priceAmount(),
        product.priceCurrency(),
        product.craftType(),
        product.imageUrl(),
        artisan.displayName(),
        artisan.craftType(),
        artisan.region());
  }
}
