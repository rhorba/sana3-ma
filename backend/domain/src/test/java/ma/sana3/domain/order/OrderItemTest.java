package ma.sana3.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderItemTest {

  @Test
  void createBuildsAnUncompletedItem() {
    UUID orderId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();

    OrderItem item =
        OrderItem.create(
            orderId,
            productId,
            "Zellige Tile Set",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            artisanProfileId,
            2);

    assertEquals(orderId, item.orderId());
    assertEquals(productId, item.productId());
    assertEquals(2, item.quantity());
    assertFalse(item.isCompleted());
    assertNotNull(item.id());
  }

  @Test
  void lineTotalMultipliesPriceByQuantity() {
    OrderItem item =
        OrderItem.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Zellige Tile Set",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            UUID.randomUUID(),
            3);

    assertEquals(new BigDecimal("1350.00"), item.lineTotal());
  }

  @Test
  void completeSetsCompletedAt() {
    OrderItem item =
        OrderItem.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Zellige Tile Set",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            UUID.randomUUID(),
            1);

    OrderItem completed = item.complete();

    assertTrue(completed.isCompleted());
    assertNotNull(completed.completedAt());
  }

  @Test
  void completeRejectsAnAlreadyCompletedItem() {
    OrderItem completed =
        OrderItem.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Zellige Tile Set",
                new BigDecimal("450.00"),
                "MAD",
                "Pottery",
                UUID.randomUUID(),
                1)
            .complete();

    assertThrows(IllegalStateException.class, completed::complete);
  }

  @Test
  void constructorRejectsNonPositivePrice() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new OrderItem(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Name",
                new BigDecimal("0.00"),
                "MAD",
                "Craft",
                UUID.randomUUID(),
                1,
                null,
                Instant.now()));
  }

  @Test
  void constructorRejectsNonPositiveQuantity() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new OrderItem(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Name",
                new BigDecimal("10.00"),
                "MAD",
                "Craft",
                UUID.randomUUID(),
                0,
                null,
                Instant.now()));
  }

  @Test
  void constructorAllowsNullProductId() {
    OrderItem item =
        new OrderItem(
            UUID.randomUUID(),
            UUID.randomUUID(),
            null,
            "Name",
            new BigDecimal("10.00"),
            "MAD",
            "Craft",
            UUID.randomUUID(),
            1,
            null,
            Instant.now());

    assertNull(item.productId());
  }
}
