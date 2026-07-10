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
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.ImageStorage;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UploadProductImageHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;
  @Mock private ImageStorage imageStorage;

  private UploadProductImageHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new UploadProductImageHandler(productRepository, artisanProfileRepository, imageStorage);
  }

  @Test
  void storesImageAndUpdatesProductImageUrl() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Product existing =
        Product.create(profile.id(), "Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    byte[] content = "fake-image-bytes".getBytes();
    UploadProductImageCommand command =
        new UploadProductImageCommand(userId, Role.ARTISAN, existing.id(), content, "image/jpeg");
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.findById(existing.id())).thenReturn(Optional.of(existing));
    when(imageStorage.store(content, "image/jpeg")).thenReturn("generated-key.jpg");
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ProductResult result = handler.handle(command);

    assertEquals("/api/v1/products/images/generated-key.jpg", result.imageUrl());
    verify(imageStorage, never()).delete(any());
  }

  @Test
  void deletesPreviousImageWhenReplacingOne() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Product existing =
        Product.create(
            profile.id(),
            "Name",
            null,
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            "/api/v1/products/images/old-key.jpg");
    byte[] content = "fake-image-bytes".getBytes();
    UploadProductImageCommand command =
        new UploadProductImageCommand(userId, Role.ARTISAN, existing.id(), content, "image/png");
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.findById(existing.id())).thenReturn(Optional.of(existing));
    when(imageStorage.store(content, "image/png")).thenReturn("new-key.png");
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    handler.handle(command);

    verify(imageStorage).delete("old-key.jpg");
  }

  @Test
  void rejectsUnsupportedContentType() {
    UUID userId = UUID.randomUUID();
    UploadProductImageCommand command =
        new UploadProductImageCommand(
            userId, Role.ARTISAN, UUID.randomUUID(), "content".getBytes(), "application/pdf");

    assertThrows(UnsupportedImageTypeException.class, () -> handler.handle(command));

    verify(imageStorage, never()).store(any(), any());
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();
    UploadProductImageCommand command =
        new UploadProductImageCommand(
            userId, Role.BUYER, UUID.randomUUID(), "content".getBytes(), "image/jpeg");

    assertThrows(NotAnArtisanException.class, () -> handler.handle(command));
  }

  @Test
  void rejectsUploadForSomeoneElsesProduct() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Product othersProduct =
        Product.create(
            UUID.randomUUID(), "Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    UploadProductImageCommand command =
        new UploadProductImageCommand(
            userId, Role.ARTISAN, othersProduct.id(), "content".getBytes(), "image/jpeg");
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(productRepository.findById(othersProduct.id())).thenReturn(Optional.of(othersProduct));

    assertThrows(ProductNotFoundException.class, () -> handler.handle(command));
  }
}
