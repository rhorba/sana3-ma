package ma.sana3.adapter.persistence.order;

import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderStatus;

final class OrderEntityMapper {

  private OrderEntityMapper() {}

  static Order toDomain(OrderJpaEntity entity) {
    return new Order(
        entity.getId(),
        entity.getBuyerUserId(),
        OrderStatus.valueOf(entity.getStatus()),
        entity.getShippingAddress(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static OrderJpaEntity toEntity(Order order) {
    return new OrderJpaEntity(
        order.id(),
        order.buyerUserId(),
        order.status().name(),
        order.shippingAddress(),
        order.createdAt(),
        order.updatedAt());
  }
}
