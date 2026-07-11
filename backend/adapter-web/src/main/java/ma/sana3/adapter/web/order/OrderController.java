package ma.sana3.adapter.web.order;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.order.CancelMyOrderCommand;
import ma.sana3.application.order.CancelMyOrderHandler;
import ma.sana3.application.order.GetMyOrderDetailHandler;
import ma.sana3.application.order.GetMyOrderDetailQuery;
import ma.sana3.application.order.ListMyOrdersHandler;
import ma.sana3.application.order.ListMyOrdersQuery;
import ma.sana3.application.order.OrderResult;
import ma.sana3.application.order.PlaceOrderCommand;
import ma.sana3.application.order.PlaceOrderHandler;
import ma.sana3.application.order.PlaceOrderLineItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
class OrderController {

  private final PlaceOrderHandler placeOrderHandler;
  private final ListMyOrdersHandler listMyOrdersHandler;
  private final GetMyOrderDetailHandler getMyOrderDetailHandler;
  private final CancelMyOrderHandler cancelMyOrderHandler;

  OrderController(
      PlaceOrderHandler placeOrderHandler,
      ListMyOrdersHandler listMyOrdersHandler,
      GetMyOrderDetailHandler getMyOrderDetailHandler,
      CancelMyOrderHandler cancelMyOrderHandler) {
    this.placeOrderHandler = placeOrderHandler;
    this.listMyOrdersHandler = listMyOrdersHandler;
    this.getMyOrderDetailHandler = getMyOrderDetailHandler;
    this.cancelMyOrderHandler = cancelMyOrderHandler;
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

  @GetMapping("/me")
  List<OrderResponse> listMine(@AuthenticationPrincipal UUID userId) {
    return listMyOrdersHandler.handle(new ListMyOrdersQuery(userId)).stream()
        .map(OrderResponse::from)
        .toList();
  }

  @GetMapping("/me/{id}")
  OrderResponse getMine(@AuthenticationPrincipal UUID userId, @PathVariable UUID id) {
    return OrderResponse.from(
        getMyOrderDetailHandler.handle(new GetMyOrderDetailQuery(userId, id)));
  }

  @PostMapping("/me/{id}/cancel")
  OrderResponse cancelMine(@AuthenticationPrincipal UUID userId, @PathVariable UUID id) {
    return OrderResponse.from(cancelMyOrderHandler.handle(new CancelMyOrderCommand(userId, id)));
  }
}
