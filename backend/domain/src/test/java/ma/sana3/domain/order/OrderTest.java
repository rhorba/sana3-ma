package ma.sana3.domain.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {

  @Test
  void placeBuildsAPlacedOrder() {
    UUID buyerUserId = UUID.randomUUID();

    Order order = Order.place(buyerUserId, "123 Rue Example, Fes");

    assertEquals(buyerUserId, order.buyerUserId());
    assertEquals(OrderStatus.PLACED, order.status());
    assertNotNull(order.id());
    assertNotNull(order.createdAt());
  }

  @Test
  void cancelTransitionsFromPlacedToCancelled() {
    Order order = Order.place(UUID.randomUUID(), "Address");

    Order cancelled = order.cancel();

    assertEquals(OrderStatus.CANCELLED, cancelled.status());
    assertEquals(order.id(), cancelled.id());
  }

  @Test
  void completeTransitionsFromPlacedToCompleted() {
    Order order = Order.place(UUID.randomUUID(), "Address");

    Order completed = order.complete();

    assertEquals(OrderStatus.COMPLETED, completed.status());
  }

  @Test
  void cancelRejectsAnAlreadyCompletedOrder() {
    Order completed = Order.place(UUID.randomUUID(), "Address").complete();

    assertThrows(IllegalOrderStatusTransitionException.class, completed::cancel);
  }

  @Test
  void cancelRejectsAnAlreadyCancelledOrder() {
    Order cancelled = Order.place(UUID.randomUUID(), "Address").cancel();

    assertThrows(IllegalOrderStatusTransitionException.class, cancelled::cancel);
  }

  @Test
  void constructorRejectsBlankShippingAddress() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OrderStatus.PLACED,
                " ",
                Instant.now(),
                Instant.now()));
  }
}
