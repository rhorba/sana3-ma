package ma.sana3.application.order;

import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.user.User;

final class ArtisanOrderItemResultMapper {

  private ArtisanOrderItemResultMapper() {}

  static ArtisanOrderItemResult toResult(OrderItem item, Order order, User buyer) {
    return new ArtisanOrderItemResult(
        item.id(),
        order.id(),
        order.status(),
        order.shippingAddress(),
        buyer.email(),
        item.productId(),
        item.productNameSnapshot(),
        item.priceAmountSnapshot(),
        item.priceCurrencySnapshot(),
        item.craftTypeSnapshot(),
        item.quantity(),
        item.lineTotal(),
        item.isCompleted(),
        item.completedAt(),
        order.createdAt());
  }
}
