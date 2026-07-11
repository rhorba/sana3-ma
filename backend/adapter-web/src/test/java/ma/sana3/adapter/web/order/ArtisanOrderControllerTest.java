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
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.order.ArtisanOrderItemResult;
import ma.sana3.application.order.CompleteArtisanOrderItemHandler;
import ma.sana3.application.order.ListArtisanOrderItemsHandler;
import ma.sana3.application.order.OrderItemNotFoundException;
import ma.sana3.domain.order.OrderItemAlreadyCompletedException;
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

@WebMvcTest(controllers = ArtisanOrderController.class)
class ArtisanOrderControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ListArtisanOrderItemsHandler listArtisanOrderItemsHandler;

  @MockitoBean private CompleteArtisanOrderItemHandler completeArtisanOrderItemHandler;

  private static RequestPostProcessor asArtisan(UUID userId) {
    return SecurityMockMvcRequestPostProcessors.authentication(
        new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority("ROLE_ARTISAN"))));
  }

  private static RequestPostProcessor asBuyer(UUID userId) {
    return SecurityMockMvcRequestPostProcessors.authentication(
        new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority("ROLE_BUYER"))));
  }

  private static ArtisanOrderItemResult stubResult(boolean completed) {
    Instant now = Instant.now();
    return new ArtisanOrderItemResult(
        UUID.randomUUID(),
        UUID.randomUUID(),
        OrderStatus.PLACED,
        "123 Rue Example, Fes",
        "buyer@example.com",
        UUID.randomUUID(),
        "Zellige Tile Set",
        new BigDecimal("450.00"),
        "MAD",
        "Pottery",
        2,
        new BigDecimal("900.00"),
        completed,
        completed ? now : null,
        now);
  }

  @Test
  void listReturnsOrderItemsForTheArtisansOwnProducts() throws Exception {
    UUID userId = UUID.randomUUID();
    when(listArtisanOrderItemsHandler.handle(any())).thenReturn(List.of(stubResult(false)));

    mockMvc
        .perform(get("/api/v1/artisan-profiles/me/orders").with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].buyerEmail").value("buyer@example.com"))
        .andExpect(jsonPath("$[0].shippingAddress").value("123 Rue Example, Fes"));
  }

  @Test
  void listRejectsNonArtisanRole() throws Exception {
    UUID userId = UUID.randomUUID();
    when(listArtisanOrderItemsHandler.handle(any())).thenThrow(new NotAnArtisanException());

    mockMvc
        .perform(get("/api/v1/artisan-profiles/me/orders").with(asBuyer(userId)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("NOT_AN_ARTISAN"));
  }

  @Test
  void completeReturnsCompletedOrderItem() throws Exception {
    UUID userId = UUID.randomUUID();
    when(completeArtisanOrderItemHandler.handle(any())).thenReturn(stubResult(true));

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/orders/" + UUID.randomUUID() + "/complete")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.completed").value(true));
  }

  @Test
  void completeReturnsNotFoundForAnotherArtisansOrderItem() throws Exception {
    UUID userId = UUID.randomUUID();
    when(completeArtisanOrderItemHandler.handle(any())).thenThrow(new OrderItemNotFoundException());

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/orders/" + UUID.randomUUID() + "/complete")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("ORDER_ITEM_NOT_FOUND"));
  }

  @Test
  void completeRejectsAnAlreadyCompletedItem() throws Exception {
    UUID userId = UUID.randomUUID();
    when(completeArtisanOrderItemHandler.handle(any()))
        .thenThrow(new OrderItemAlreadyCompletedException());

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/orders/" + UUID.randomUUID() + "/complete")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ORDER_ITEM_ALREADY_COMPLETED"));
  }
}
