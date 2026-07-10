package ma.sana3.application.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
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
class CreateProductHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private CreateProductHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreateProductHandler(productRepository, artisanProfileRepository);
  }

  @Test
  void createsProductForArtisanWithProfile() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    CreateProductCommand command =
        new CreateProductCommand(
            userId,
            Role.ARTISAN,
            "Zellige Tile Set",
            "Handmade",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            null);
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ProductResult result = handler.handle(command);

    assertEquals(profile.id(), result.artisanProfileId());
    assertEquals("Zellige Tile Set", result.name());
    assertEquals(new BigDecimal("450.00"), result.priceAmount());
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();
    CreateProductCommand command =
        new CreateProductCommand(
            userId, Role.BUYER, "Name", null, new BigDecimal("10.00"), "MAD", "Craft", null);

    assertThrows(NotAnArtisanException.class, () -> handler.handle(command));

    verify(productRepository, never()).save(any());
  }

  @Test
  void rejectsArtisanWithoutProfile() {
    UUID userId = UUID.randomUUID();
    CreateProductCommand command =
        new CreateProductCommand(
            userId, Role.ARTISAN, "Name", null, new BigDecimal("10.00"), "MAD", "Craft", null);
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(ProfileNotFoundException.class, () -> handler.handle(command));

    verify(productRepository, never()).save(any());
  }
}
