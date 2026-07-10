package ma.sana3.domain.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  void createBuildsProductForArtisanProfile() {
    UUID artisanProfileId = UUID.randomUUID();

    Product product =
        Product.create(
            artisanProfileId,
            "Zellige Tile Set",
            "Handmade blue zellige tiles",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            null);

    assertEquals(artisanProfileId, product.artisanProfileId());
    assertEquals("Zellige Tile Set", product.name());
    assertEquals(new BigDecimal("450.00"), product.priceAmount());
    assertEquals("MAD", product.priceCurrency());
    assertEquals("Pottery", product.craftType());
    assertNotNull(product.id());
    assertNotNull(product.createdAt());
    assertNotNull(product.updatedAt());
  }

  @Test
  void withDetailsKeepsIdentityAndBumpsUpdatedAt() {
    Product product =
        Product.create(
            UUID.randomUUID(), "Name", null, new BigDecimal("10.00"), "MAD", "Craft", null);

    Product updated =
        product.withDetails(
            "New Name", "New description", new BigDecimal("20.00"), "MAD", "New Craft", "img.jpg");

    assertEquals(product.id(), updated.id());
    assertEquals(product.artisanProfileId(), updated.artisanProfileId());
    assertEquals(product.createdAt(), updated.createdAt());
    assertEquals("New Name", updated.name());
    assertEquals(new BigDecimal("20.00"), updated.priceAmount());
    assertEquals("img.jpg", updated.imageUrl());
  }

  @Test
  void constructorRejectsBlankName() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Product(
                UUID.randomUUID(),
                UUID.randomUUID(),
                " ",
                null,
                new BigDecimal("10.00"),
                "MAD",
                "Craft",
                null,
                Instant.now(),
                Instant.now()));
  }

  @Test
  void constructorRejectsNonPositivePrice() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Product(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Name",
                null,
                new BigDecimal("0.00"),
                "MAD",
                "Craft",
                null,
                Instant.now(),
                Instant.now()));
  }

  @Test
  void constructorRejectsBlankCraftType() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Product(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Name",
                null,
                new BigDecimal("10.00"),
                "MAD",
                "",
                null,
                Instant.now(),
                Instant.now()));
  }
}
