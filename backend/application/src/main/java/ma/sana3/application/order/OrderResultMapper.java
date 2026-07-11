package ma.sana3.application.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;

final class OrderResultMapper {

  private OrderResultMapper() {}

  static OrderResult toResult(Order order, List<OrderItem> items) {
    List<OrderItemResult> itemResults =
        items.stream().map(OrderResultMapper::toItemResult).toList();
    return new OrderResult(
        order.id(),
        order.buyerUserId(),
        order.status(),
        order.shippingAddress(),
        itemResults,
        totalsByCurrency(items),
        order.createdAt(),
        order.updatedAt());
  }

  private static OrderItemResult toItemResult(OrderItem item) {
    return new OrderItemResult(
        item.id(),
        item.productId(),
        item.productNameSnapshot(),
        item.priceAmountSnapshot(),
        item.priceCurrencySnapshot(),
        item.craftTypeSnapshot(),
        item.artisanProfileId(),
        item.quantity(),
        item.lineTotal(),
        item.isCompleted(),
        item.completedAt());
  }

  private static List<OrderTotal> totalsByCurrency(List<OrderItem> items) {
    Map<String, BigDecimal> totals = new TreeMap<>();
    for (OrderItem item : items) {
      totals.merge(item.priceCurrencySnapshot(), item.lineTotal(), BigDecimal::add);
    }
    return totals.entrySet().stream()
        .map(entry -> new OrderTotal(entry.getKey(), entry.getValue()))
        .toList();
  }
}
