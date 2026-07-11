package ma.sana3.application.order;

import java.util.List;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CancelMyOrderHandler {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  public CancelMyOrderHandler(
      OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
  }

  public OrderResult handle(CancelMyOrderCommand command) {
    Order order =
        orderRepository
            .findById(command.orderId())
            .filter(existing -> existing.buyerUserId().equals(command.buyerUserId()))
            .orElseThrow(OrderNotFoundException::new);

    List<OrderItem> items = orderItemRepository.findByOrderId(order.id());
    if (items.stream().anyMatch(OrderItem::isCompleted)) {
      throw new OrderHasCompletedItemsException();
    }

    Order cancelled = orderRepository.save(order.cancel());
    return OrderResultMapper.toResult(cancelled, items);
  }
}
