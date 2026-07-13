package ma.sana3.application.catalog;

import java.util.Set;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.catalog.ImageStorage;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class UploadProductImageHandler {

  // SVG is deliberately excluded even though it's an "image" -- it can carry embedded scripts and
  // is a well-known image-upload XSS vector if ever rendered inline.
  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/jpeg", "image/png", "image/webp");

  private final ProductRepository productRepository;
  private final CooperativeMembershipRepository membershipRepository;
  private final ImageStorage imageStorage;

  public UploadProductImageHandler(
      ProductRepository productRepository,
      CooperativeMembershipRepository membershipRepository,
      ImageStorage imageStorage) {
    this.productRepository = productRepository;
    this.membershipRepository = membershipRepository;
    this.imageStorage = imageStorage;
  }

  public ProductResult handle(UploadProductImageCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    if (!ALLOWED_CONTENT_TYPES.contains(command.contentType())) {
      throw new UnsupportedImageTypeException(command.contentType());
    }
    var artisanProfileId =
        membershipRepository
            .findByUserId(command.userId())
            .map(CooperativeMembership::artisanProfileId)
            .orElseThrow(ProfileNotFoundException::new);
    Product existing =
        productRepository
            .findById(command.productId())
            .filter(product -> product.artisanProfileId().equals(artisanProfileId))
            .orElseThrow(ProductNotFoundException::new);

    String storageKey = imageStorage.store(command.content(), command.contentType());
    if (existing.imageUrl() != null) {
      imageStorage.delete(ProductImageUrls.toStorageKey(existing.imageUrl()));
    }

    Product updated =
        existing.withDetails(
            existing.name(),
            existing.description(),
            existing.priceAmount(),
            existing.priceCurrency(),
            existing.craftType(),
            ProductImageUrls.toUrl(storageKey));

    Product saved = productRepository.save(updated);
    return ProductResultMapper.toResult(saved);
  }
}
