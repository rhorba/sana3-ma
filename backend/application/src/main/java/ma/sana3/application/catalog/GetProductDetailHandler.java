package ma.sana3.application.catalog;

import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class GetProductDetailHandler {

  private final ProductRepository productRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public GetProductDetailHandler(
      ProductRepository productRepository, ArtisanProfileRepository artisanProfileRepository) {
    this.productRepository = productRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public PublicProductSummary handle(GetProductDetailQuery query) {
    Product product =
        productRepository.findById(query.productId()).orElseThrow(ProductNotFoundException::new);
    ArtisanProfile artisan =
        artisanProfileRepository
            .findById(product.artisanProfileId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Product references a missing artisan profile: "
                            + product.artisanProfileId()));
    return PublicProductSummaryMapper.toSummary(product, artisan);
  }
}
