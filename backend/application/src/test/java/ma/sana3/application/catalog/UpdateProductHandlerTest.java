package ma.sana3.application.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
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
class UpdateProductHandlerTest {

  @Mock private ProductRepository productRepository;
  @Mock private CooperativeMembershipRepository membershipRepository;

  private UpdateProductHandler handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateProductHandler(productRepository, membershipRepository);
  }

  @Test
  void updatesOwnProduct() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product existing =
        Product.create(
            artisanProfileId, "Old Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null);
    UpdateProductCommand command =
        new UpdateProductCommand(
            userId,
            Role.ARTISAN,
            existing.id(),
            "New Name",
            "New description",
            new BigDecimal("20.00"),
            "MAD",
            "Pottery");
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.OWNER)));
    when(productRepository.findById(existing.id())).thenReturn(Optional.of(existing));
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ProductResult result = handler.handle(command);

    assertEquals(existing.id(), result.id());
    assertEquals("New Name", result.name());
    assertEquals(new BigDecimal("20.00"), result.priceAmount());
  }

  @Test
  void preservesExistingImageOnTextOnlyUpdate() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product existing =
        Product.create(
            artisanProfileId,
            "Old Name",
            null,
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            "/api/v1/products/images/existing.jpg");
    UpdateProductCommand command =
        new UpdateProductCommand(
            userId,
            Role.ARTISAN,
            existing.id(),
            "New Name",
            null,
            new BigDecimal("20.00"),
            "MAD",
            "Pottery");
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.OWNER)));
    when(productRepository.findById(existing.id())).thenReturn(Optional.of(existing));
    when(productRepository.save(any(Product.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ProductResult result = handler.handle(command);

    assertEquals("/api/v1/products/images/existing.jpg", result.imageUrl());
  }

  @Test
  void rejectsUpdatingSomeoneElsesProduct() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
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
            "Pottery");
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.OWNER)));
    when(productRepository.findById(othersProduct.id())).thenReturn(Optional.of(othersProduct));

    assertThrows(ProductNotFoundException.class, () -> handler.handle(command));
  }
}
