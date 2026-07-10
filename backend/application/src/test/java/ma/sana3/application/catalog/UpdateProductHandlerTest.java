package ma.sana3.application.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
class UpdateProductHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private UpdateProductHandler handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateProductHandler(productRepository, artisanProfileRepository);
  }

  @Test
  void updatesOwnProduct() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Product existing =
        Product.create(
            profile.id(), "Old Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    UpdateProductCommand command =
        new UpdateProductCommand(
            userId,
            Role.ARTISAN,
            existing.id(),
            "New Name",
            "New description",
            new BigDecimal("20.00"),
            "MAD",
            "Pottery",
            null);
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.findById(existing.id())).thenReturn(Optional.of(existing));
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ProductResult result = handler.handle(command);

    assertEquals(existing.id(), result.id());
    assertEquals("New Name", result.name());
    assertEquals(new BigDecimal("20.00"), result.priceAmount());
  }

  @Test
  void rejectsUpdatingSomeoneElsesProduct() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Product othersProduct =
        Product.create(
            UUID.randomUUID(), "Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    UpdateProductCommand command =
        new UpdateProductCommand(
            userId,
            Role.ARTISAN,
            othersProduct.id(),
            "New Name",
            null,
            new BigDecimal("20.00"),
            "MAD",
            "Pottery",
            null);
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.findById(othersProduct.id())).thenReturn(Optional.of(othersProduct));

    assertThrows(ProductNotFoundException.class, () -> handler.handle(command));
  }
}
