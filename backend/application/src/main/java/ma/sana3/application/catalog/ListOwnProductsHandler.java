package ma.sana3.application.catalog;

import java.util.List;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class ListOwnProductsHandler {

  private final ProductRepository productRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public ListOwnProductsHandler(
      ProductRepository productRepository, ArtisanProfileRepository artisanProfileRepository) {
    this.productRepository = productRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public List<ProductResult> handle(ListOwnProductsQuery query) {
    if (query.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    ArtisanProfile profile =
        artisanProfileRepository
            .findByUserId(query.userId())
            .orElseThrow(ProfileNotFoundException::new);

    return productRepository.findByArtisanProfileId(profile.id()).stream()
        .map(ProductResultMapper::toResult)
        .toList();
  }
}
