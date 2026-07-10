package ma.sana3.application.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetProductDetailHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private GetProductDetailHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetProductDetailHandler(productRepository, artisanProfileRepository);
  }

  @Test
  void returnsProductWithArtisanSummary() {
    ArtisanProfile artisan =
        ArtisanProfile.create(UUID.randomUUID(), "Fatima Zahra", "Pottery", "Fes", null, null);
    Product product =
        Product.create(
            artisan.id(),
            "Zellige Tile Set",
            null,
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            null);
    when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
    when(artisanProfileRepository.findById(artisan.id())).thenReturn(Optional.of(artisan));

    PublicProductSummary result = handler.handle(new GetProductDetailQuery(product.id()));

    assertEquals("Zellige Tile Set", result.name());
    assertEquals("Fatima Zahra", result.artisanDisplayName());
  }

  @Test
  void throwsWhenProductMissing() {
    UUID productId = UUID.randomUUID();
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    assertThrows(
        ProductNotFoundException.class, () -> handler.handle(new GetProductDetailQuery(productId)));
  }
}
