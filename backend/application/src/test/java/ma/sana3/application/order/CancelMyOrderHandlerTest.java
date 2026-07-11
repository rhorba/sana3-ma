package ma.sana3.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.order.IllegalOrderStatusTransitionException;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import ma.sana3.domain.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancelMyOrderHandlerTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderItemRepository orderItemRepository;

  private CancelMyOrderHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CancelMyOrderHandler(orderRepository, orderItemRepository);
  }

  @Test
  void cancelsAnOwnedPlacedOrder() {
    UUID buyerUserId = UUID.randomUUID();
    Order order = Order.place(buyerUserId, "Address");
    when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));
    when(orderRepository.save(any(Order.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(orderItemRepository.findByOrderId(order.id())).thenReturn(List.of());

    OrderResult result = handler.handle(new CancelMyOrderCommand(buyerUserId, order.id()));

    assertEquals(OrderStatus.CANCELLED, result.status());
  }

  @Test
  void rejectsCancellingAnOrderOwnedBySomeoneElse() {
    UUID buyerUserId = UUID.randomUUID();
    Order order = Order.place(UUID.randomUUID(), "Address");
    when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));

    assertThrows(
        OrderNotFoundException.class,
        () -> handler.handle(new CancelMyOrderCommand(buyerUserId, order.id())));
  }

  @Test
  void rejectsCancellingAnAlreadyCompletedOrder() {
    UUID buyerUserId = UUID.randomUUID();
    Order completed = Order.place(buyerUserId, "Address").complete();
    when(orderRepository.findById(completed.id())).thenReturn(Optional.of(completed));

    assertThrows(
        IllegalOrderStatusTransitionException.class,
        () -> handler.handle(new CancelMyOrderCommand(buyerUserId, completed.id())));
  }

  @Test
  void rejectsCancellingAnOrderWithAFulfilledItem() {
    UUID buyerUserId = UUID.randomUUID();
    Order order = Order.place(buyerUserId, "Address");
    OrderItem fulfilled =
        OrderItem.create(
                order.id(),
                UUID.randomUUID(),
                "Tile",
                new BigDecimal("10.00"),
                "MAD",
                "Pottery",
                UUID.randomUUID(),
                1)
            .complete();
    when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));
    when(orderItemRepository.findByOrderId(order.id())).thenReturn(List.of(fulfilled));

    assertThrows(
        OrderHasCompletedItemsException.class,
        () -> handler.handle(new CancelMyOrderCommand(buyerUserId, order.id())));
  }
}
