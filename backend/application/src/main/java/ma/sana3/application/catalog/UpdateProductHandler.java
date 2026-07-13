package ma.sana3.application.catalog;

import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductHandler {

  private final ProductRepository productRepository;
  private final CooperativeMembershipRepository membershipRepository;

  public UpdateProductHandler(
      ProductRepository productRepository, CooperativeMembershipRepository membershipRepository) {
    this.productRepository = productRepository;
    this.membershipRepository = membershipRepository;
  }

  public ProductResult handle(UpdateProductCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
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

    Product updated =
        existing.withDetails(
            command.name(),
            command.description(),
            command.priceAmount(),
            command.priceCurrency(),
            command.craftType(),
            existing.imageUrl());

    Product saved = productRepository.save(updated);
    return ProductResultMapper.toResult(saved);
  }
}
