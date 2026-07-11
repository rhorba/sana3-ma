package ma.sana3.adapter.web.order;

import java.math.BigDecimal;
import ma.sana3.application.order.OrderTotal;

public record OrderTotalResponse(String currency, BigDecimal amount) {
  static OrderTotalResponse from(OrderTotal total) {
    return new OrderTotalResponse(total.currency(), total.amount());
  }
}
