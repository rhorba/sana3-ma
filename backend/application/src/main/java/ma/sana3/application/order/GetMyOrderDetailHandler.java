package ma.sana3.application.order;

import java.util.UUID;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class GetMyOrderDetailHandler {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  public GetMyOrderDetailHandler(
      OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
  }

  public OrderResult handle(GetMyOrderDetailQuery query) {
    Order order = findOwnedOrder(query.buyerUserId(), query.orderId());
    return OrderResultMapper.toResult(order, orderItemRepository.findByOrderId(order.id()));
  }

  private Order findOwnedOrder(UUID buyerUserId, UUID orderId) {
    return orderRepository
        .findById(orderId)
        .filter(order -> order.buyerUserId().equals(buyerUserId))
        .orElseThrow(OrderNotFoundException::new);
  }
}
