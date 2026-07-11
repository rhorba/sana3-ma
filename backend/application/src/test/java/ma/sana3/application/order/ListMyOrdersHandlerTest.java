package ma.sana3.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListMyOrdersHandlerTest {

  @Mock private OrderRepository orderRepository;
  @Mock private OrderItemRepository orderItemRepository;

  private ListMyOrdersHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ListMyOrdersHandler(orderRepository, orderItemRepository);
  }

  @Test
  void listsOrdersWithTheirItems() {
    UUID buyerUserId = UUID.randomUUID();
    Order order = Order.place(buyerUserId, "Address");
    OrderItem item =
        OrderItem.create(
            order.id(),
            UUID.randomUUID(),
            "Tile",
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            UUID.randomUUID(),
            1);
    when(orderRepository.findByBuyerUserId(buyerUserId)).thenReturn(List.of(order));
    when(orderItemRepository.findByOrderId(order.id())).thenReturn(List.of(item));

    List<OrderResult> results = handler.handle(new ListMyOrdersQuery(buyerUserId));

    assertEquals(1, results.size());
    assertEquals(1, results.get(0).items().size());
  }

  @Test
  void returnsEmptyListForABuyerWithNoOrders() {
    UUID buyerUserId = UUID.randomUUID();
    when(orderRepository.findByBuyerUserId(buyerUserId)).thenReturn(List.of());

    List<OrderResult> results = handler.handle(new ListMyOrdersQuery(buyerUserId));

    assertEquals(0, results.size());
  }
}
