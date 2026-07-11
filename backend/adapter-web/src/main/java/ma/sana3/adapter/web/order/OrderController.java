package ma.sana3.adapter.web.order;

import jakarta.validation.Valid;
import java.util.UUID;
import ma.sana3.application.order.OrderResult;
import ma.sana3.application.order.PlaceOrderCommand;
import ma.sana3.application.order.PlaceOrderHandler;
import ma.sana3.application.order.PlaceOrderLineItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
class OrderController {

  private final PlaceOrderHandler placeOrderHandler;

  OrderController(PlaceOrderHandler placeOrderHandler) {
    this.placeOrderHandler = placeOrderHandler;
  }

  @PostMapping
  ResponseEntity<OrderResponse> place(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody PlaceOrderRequest request) {
    OrderResult result =
        placeOrderHandler.handle(
            new PlaceOrderCommand(
                userId,
                request.shippingAddress(),
                request.items().stream()
                    .map(item -> new PlaceOrderLineItem(item.productId(), item.quantity()))
                    .toList()));
    return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(result));
  }
}
