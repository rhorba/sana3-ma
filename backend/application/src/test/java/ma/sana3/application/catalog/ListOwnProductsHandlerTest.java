package ma.sana3.application.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListOwnProductsHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private CooperativeMembershipRepository membershipRepository;

  private ListOwnProductsHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ListOwnProductsHandler(productRepository, membershipRepository);
  }

  @Test
  void returnsOwnProducts() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product product =
        Product.create(
            artisanProfileId, "Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.MEMBER)));
    when(productRepository.findByArtisanProfileId(artisanProfileId)).thenReturn(List.of(product));

    List<ProductResult> results = handler.handle(new ListOwnProductsQuery(userId, Role.ARTISAN));

    assertEquals(1, results.size());
    assertEquals(product.id(), results.get(0).id());
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();

    assertThrows(
        NotAnArtisanException.class,
        () -> handler.handle(new ListOwnProductsQuery(userId, Role.BUYER)));
  }
}
