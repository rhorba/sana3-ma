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
public class CreateProductHandler {

  private final ProductRepository productRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public CreateProductHandler(
      ProductRepository productRepository, ArtisanProfileRepository artisanProfileRepository) {
    this.productRepository = productRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public ProductResult handle(CreateProductCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    ArtisanProfile profile =
        artisanProfileRepository
            .findByUserId(command.userId())
            .orElseThrow(ProfileNotFoundException::new);

    Product product =
        Product.create(
            profile.id(),
            command.name(),
            command.description(),
            command.priceAmount(),
            command.priceCurrency(),
            command.craftType(),
            command.imageUrl());

    Product saved = productRepository.save(product);
    return ProductResultMapper.toResult(saved);
  }
}
