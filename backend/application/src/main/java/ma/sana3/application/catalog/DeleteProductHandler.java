package ma.sana3.application.catalog;

import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class DeleteProductHandler {

  private final ProductRepository productRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public DeleteProductHandler(
      ProductRepository productRepository, ArtisanProfileRepository artisanProfileRepository) {
    this.productRepository = productRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public void handle(DeleteProductCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    ArtisanProfile profile =
        artisanProfileRepository
            .findByUserId(command.userId())
            .orElseThrow(ProfileNotFoundException::new);

    Product existing =
        productRepository
            .findById(command.productId())
            .filter(product -> product.artisanProfileId().equals(profile.id()))
            .orElseThrow(ProductNotFoundException::new);

    productRepository.deleteById(existing.id());
  }
}
