package ma.sana3.application.catalog;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteProductHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private DeleteProductHandler handler;

  @BeforeEach
  void setUp() {
    handler = new DeleteProductHandler(productRepository, artisanProfileRepository);
  }

  @Test
  void deletesOwnProduct() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Product existing =
        Product.create(profile.id(), "Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.findById(existing.id())).thenReturn(Optional.of(existing));

    handler.handle(new DeleteProductCommand(userId, Role.ARTISAN, existing.id()));

    verify(productRepository).deleteById(existing.id());
  }

  @Test
  void rejectsDeletingSomeoneElsesProduct() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Product othersProduct =
        Product.create(
            UUID.randomUUID(), "Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.findById(othersProduct.id())).thenReturn(Optional.of(othersProduct));

    assertThrows(
        ProductNotFoundException.class,
        () -> handler.handle(new DeleteProductCommand(userId, Role.ARTISAN, othersProduct.id())));

    verify(productRepository, never()).deleteById(any());
  }
}
