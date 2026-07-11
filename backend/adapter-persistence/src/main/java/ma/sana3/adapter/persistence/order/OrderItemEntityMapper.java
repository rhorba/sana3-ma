package ma.sana3.adapter.persistence.order;

import ma.sana3.domain.order.OrderItem;

final class OrderItemEntityMapper {

  private OrderItemEntityMapper() {}

  static OrderItem toDomain(OrderItemJpaEntity entity) {
    return new OrderItem(
        entity.getId(),
        entity.getOrderId(),
        entity.getProductId(),
        entity.getProductNameSnapshot(),
        entity.getPriceAmountSnapshot(),
        entity.getPriceCurrencySnapshot(),
        entity.getCraftTypeSnapshot(),
        entity.getArtisanProfileId(),
        entity.getQuantity(),
        entity.getCompletedAt(),
        entity.getCreatedAt());
  }

  static OrderItemJpaEntity toEntity(OrderItem item) {
    return new OrderItemJpaEntity(
        item.id(),
        item.orderId(),
        item.productId(),
        item.productNameSnapshot(),
        item.priceAmountSnapshot(),
        item.priceCurrencySnapshot(),
        item.craftTypeSnapshot(),
        item.artisanProfileId(),
        item.quantity(),
        item.completedAt(),
        item.createdAt());
  }
}
