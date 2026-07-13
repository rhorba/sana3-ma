package ma.sana3.application.catalog;

import java.util.List;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class ListOwnProductsHandler {

  private final ProductRepository productRepository;
  private final CooperativeMembershipRepository membershipRepository;

  public ListOwnProductsHandler(
      ProductRepository productRepository, CooperativeMembershipRepository membershipRepository) {
    this.productRepository = productRepository;
    this.membershipRepository = membershipRepository;
  }

  public List<ProductResult> handle(ListOwnProductsQuery query) {
    if (query.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    var artisanProfileId =
        membershipRepository
            .findByUserId(query.userId())
            .map(CooperativeMembership::artisanProfileId)
            .orElseThrow(ProfileNotFoundException::new);

    return productRepository.findByArtisanProfileId(artisanProfileId).stream()
        .map(ProductResultMapper::toResult)
        .toList();
  }
}
