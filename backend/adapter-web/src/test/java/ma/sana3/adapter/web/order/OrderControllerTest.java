package ma.sana3.adapter.web.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.application.order.CancelMyOrderHandler;
import ma.sana3.application.order.GetMyOrderDetailHandler;
import ma.sana3.application.order.ListMyOrdersHandler;
import ma.sana3.application.order.OrderHasCompletedItemsException;
import ma.sana3.application.order.OrderItemResult;
import ma.sana3.application.order.OrderNotFoundException;
import ma.sana3.application.order.OrderResult;
import ma.sana3.application.order.OrderTotal;
import ma.sana3.application.order.PlaceOrderHandler;
import ma.sana3.domain.order.IllegalOrderStatusTransitionException;
import ma.sana3.domain.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private PlaceOrderHandler placeOrderHandler;

  @MockitoBean private ListMyOrdersHandler listMyOrdersHandler;

  @MockitoBean private GetMyOrderDetailHandler getMyOrderDetailHandler;

  @MockitoBean private CancelMyOrderHandler cancelMyOrderHandler;

  private static RequestPostProcessor asBuyer(UUID userId) {
    return SecurityMockMvcRequestPostProcessors.authentication(
        new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority("ROLE_BUYER"))));
  }

  private static OrderResult stubResult(UUID buyerUserId) {
    Instant now = Instant.now();
    OrderItemResult item =
        new OrderItemResult(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Zellige Tile Set",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            UUID.randomUUID(),
            2,
            new BigDecimal("900.00"),
            false,
            null);
    return new OrderResult(
        UUID.randomUUID(),
        buyerUserId,
        OrderStatus.PLACED,
        "123 Rue Example, Fes",
        List.of(item),
        List.of(new OrderTotal("MAD", new BigDecimal("900.00"))),
        now,
        now);
  }

  @Test
  void placeWithValidRequestReturnsCreated() throws Exception {
    UUID userId = UUID.randomUUID();
    when(placeOrderHandler.handle(any())).thenReturn(stubResult(userId));

    mockMvc
        .perform(
            post("/api/v1/orders")
                .with(csrf())
                .with(asBuyer(userId))
                .contentType("application/json")
                .content(
                    """
                    {"shippingAddress":"123 Rue Example, Fes","items":[{"productId":"%s","quantity":2}]}
                    """
                        .formatted(UUID.randomUUID())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PLACED"))
        .andExpect(jsonPath("$.totals[0].currency").value("MAD"));
  }

  @Test
  void placeRejectsBlankShippingAddress() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            post("/api/v1/orders")
                .with(csrf())
                .with(asBuyer(userId))
                .contentType("application/json")
                .content(
                    """
                    {"shippingAddress":"","items":[{"productId":"%s","quantity":1}]}
                    """
                        .formatted(UUID.randomUUID())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
  }

  @Test
  void placeRejectsEmptyItemsList() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            post("/api/v1/orders")
                .with(csrf())
                .with(asBuyer(userId))
                .contentType("application/json")
                .content(
                    """
                    {"shippingAddress":"Address","items":[]}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
  }

  @Test
  void placeReturnsNotFoundForAMissingProduct() throws Exception {
    UUID userId = UUID.randomUUID();
    when(placeOrderHandler.handle(any())).thenThrow(new ProductNotFoundException());

    mockMvc
        .perform(
            post("/api/v1/orders")
                .with(csrf())
                .with(asBuyer(userId))
                .contentType("application/json")
                .content(
                    """
                    {"shippingAddress":"Address","items":[{"productId":"%s","quantity":1}]}
                    """
                        .formatted(UUID.randomUUID())))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("PRODUCT_NOT_FOUND"));
  }

  @Test
  void listMineReturnsOwnOrders() throws Exception {
    UUID userId = UUID.randomUUID();
    when(listMyOrdersHandler.handle(any())).thenReturn(List.of(stubResult(userId)));

    mockMvc
        .perform(get("/api/v1/orders/me").with(asBuyer(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status").value("PLACED"));
  }

  @Test
  void getMineReturnsOrderDetail() throws Exception {
    UUID userId = UUID.randomUUID();
    when(getMyOrderDetailHandler.handle(any())).thenReturn(stubResult(userId));

    mockMvc
        .perform(get("/api/v1/orders/me/" + UUID.randomUUID()).with(asBuyer(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.shippingAddress").value("123 Rue Example, Fes"));
  }

  @Test
  void getMineReturnsNotFoundForSomeoneElsesOrder() throws Exception {
    UUID userId = UUID.randomUUID();
    when(getMyOrderDetailHandler.handle(any())).thenThrow(new OrderNotFoundException());

    mockMvc
        .perform(get("/api/v1/orders/me/" + UUID.randomUUID()).with(asBuyer(userId)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("ORDER_NOT_FOUND"));
  }

  @Test
  void cancelMineReturnsCancelledOrder() throws Exception {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    OrderResult cancelled =
        new OrderResult(
            UUID.randomUUID(),
            userId,
            OrderStatus.CANCELLED,
            "Address",
            List.of(),
            List.of(),
            now,
            now);
    when(cancelMyOrderHandler.handle(any())).thenReturn(cancelled);

    mockMvc
        .perform(
            post("/api/v1/orders/me/" + UUID.randomUUID() + "/cancel")
                .with(csrf())
                .with(asBuyer(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void cancelMineRejectsAnAlreadyCompletedOrder() throws Exception {
    UUID userId = UUID.randomUUID();
    when(cancelMyOrderHandler.handle(any()))
        .thenThrow(
            new IllegalOrderStatusTransitionException(
                OrderStatus.COMPLETED, OrderStatus.CANCELLED));

    mockMvc
        .perform(
            post("/api/v1/orders/me/" + UUID.randomUUID() + "/cancel")
                .with(csrf())
                .with(asBuyer(userId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ILLEGAL_ORDER_STATUS_TRANSITION"));
  }

  @Test
  void cancelMineRejectsAnOrderWithAFulfilledItem() throws Exception {
    UUID userId = UUID.randomUUID();
    when(cancelMyOrderHandler.handle(any())).thenThrow(new OrderHasCompletedItemsException());

    mockMvc
        .perform(
            post("/api/v1/orders/me/" + UUID.randomUUID() + "/cancel")
                .with(csrf())
                .with(asBuyer(userId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ORDER_HAS_COMPLETED_ITEMS"));
  }
}
