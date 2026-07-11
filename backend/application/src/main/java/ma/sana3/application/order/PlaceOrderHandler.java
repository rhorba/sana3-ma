package ma.sana3.application.order;

import java.util.List;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaceOrderHandler {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final ProductRepository productRepository;

  public PlaceOrderHandler(
      OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      ProductRepository productRepository) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.productRepository = productRepository;
  }

  @Transactional
  public OrderResult handle(PlaceOrderCommand command) {
    Order order =
        orderRepository.save(Order.place(command.buyerUserId(), command.shippingAddress()));

    List<OrderItem> savedItems =
        command.items().stream()
            .map(line -> placeLine(order, line))
            .map(orderItemRepository::save)
            .toList();

    return OrderResultMapper.toResult(order, savedItems);
  }

  private OrderItem placeLine(Order order, PlaceOrderLineItem line) {
    Product product =
        productRepository
            .findById(line.productId())
            .orElseThrow(() -> new ProductNotFoundException(line.productId()));
    return OrderItem.create(
        order.id(),
        product.id(),
        product.name(),
        product.priceAmount(),
        product.priceCurrency(),
        product.craftType(),
        product.artisanProfileId(),
        line.quantity());
  }
}
