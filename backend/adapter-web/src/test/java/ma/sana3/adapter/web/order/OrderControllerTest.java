package ma.sana3.adapter.web.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.application.order.OrderItemResult;
import ma.sana3.application.order.OrderResult;
import ma.sana3.application.order.OrderTotal;
import ma.sana3.application.order.PlaceOrderHandler;
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
}
