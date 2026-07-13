package ma.sana3.application.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.catalog.ProductSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchProductsHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private SearchProductsHandler handler;

  @BeforeEach
  void setUp() {
    handler = new SearchProductsHandler(productRepository, artisanProfileRepository);
  }

  @Test
  void composesProductsWithTheirArtisanSummary() {
    ArtisanProfile artisan = ArtisanProfile.create("Fatima Zahra", "Pottery", "Fes", null, null);
    Product product =
        Product.create(
            artisan.id(),
            "Zellige Tile Set",
            null,
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            null);
    when(productRepository.search(any()))
        .thenReturn(new ProductSearchResult(List.of(product), 1, 0, 20));
    when(artisanProfileRepository.findByIds(any())).thenReturn(List.of(artisan));

    PublicProductPage page =
        handler.handle(new SearchProductsQuery(null, null, null, null, null, 0, 20));

    assertEquals(1, page.products().size());
    assertEquals("Zellige Tile Set", page.products().get(0).name());
    assertEquals("Fatima Zahra", page.products().get(0).artisanDisplayName());
    assertEquals("Fes", page.products().get(0).artisanRegion());
    assertEquals(1, page.totalElements());
  }

  @Test
  void clampsPageSizeToTheAllowedRange() {
    when(productRepository.search(any())).thenReturn(new ProductSearchResult(List.of(), 0, 0, 100));
    when(artisanProfileRepository.findByIds(any())).thenReturn(List.of());

    PublicProductPage page =
        handler.handle(new SearchProductsQuery(null, null, null, null, null, -5, 500));

    assertEquals(100, page.pageSize());
  }
}
