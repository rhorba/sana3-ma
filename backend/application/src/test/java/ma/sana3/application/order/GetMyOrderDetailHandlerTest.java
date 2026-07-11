package ma.sana3.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetMyOrderDetailHandlerTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderItemRepository orderItemRepository;

  private GetMyOrderDetailHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetMyOrderDetailHandler(orderRepository, orderItemRepository);
  }

  @Test
  void returnsOwnedOrderDetail() {
    UUID buyerUserId = UUID.randomUUID();
    Order order = Order.place(buyerUserId, "Address");
    when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));
    when(orderItemRepository.findByOrderId(order.id())).thenReturn(List.of());

    OrderResult result = handler.handle(new GetMyOrderDetailQuery(buyerUserId, order.id()));

    assertEquals(order.id(), result.id());
  }

  @Test
  void rejectsAnOrderOwnedBySomeoneElse() {
    UUID buyerUserId = UUID.randomUUID();
    Order order = Order.place(UUID.randomUUID(), "Address");
    when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));

    assertThrows(
        OrderNotFoundException.class,
        () -> handler.handle(new GetMyOrderDetailQuery(buyerUserId, order.id())));
  }

  @Test
  void rejectsAnUnknownOrderId() {
    UUID buyerUserId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    assertThrows(
        OrderNotFoundException.class,
        () -> handler.handle(new GetMyOrderDetailQuery(buyerUserId, orderId)));
  }
}
