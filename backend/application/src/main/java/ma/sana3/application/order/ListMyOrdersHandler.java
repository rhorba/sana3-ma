package ma.sana3.application.order;

import java.util.List;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class ListMyOrdersHandler {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  public ListMyOrdersHandler(
      OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
  }

  public List<OrderResult> handle(ListMyOrdersQuery query) {
    List<Order> orders = orderRepository.findByBuyerUserId(query.buyerUserId());
    return orders.stream()
        .map(
            order ->
                OrderResultMapper.toResult(order, orderItemRepository.findByOrderId(order.id())))
        .toList();
  }
}
