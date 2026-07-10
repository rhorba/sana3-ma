package ma.sana3.adapter.web.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.catalog.CreateProductHandler;
import ma.sana3.application.catalog.DeleteProductHandler;
import ma.sana3.application.catalog.ListOwnProductsHandler;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.application.catalog.ProductResult;
import ma.sana3.application.catalog.UpdateProductHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private CreateProductHandler createProductHandler;

  @MockitoBean private UpdateProductHandler updateProductHandler;

  @MockitoBean private DeleteProductHandler deleteProductHandler;

  @MockitoBean private ListOwnProductsHandler listOwnProductsHandler;

  private static ProductResult stubResult(UUID artisanProfileId) {
    Instant now = Instant.now();
    return new ProductResult(
        UUID.randomUUID(),
        artisanProfileId,
        "Zellige Tile Set",
        "Handmade",
        new BigDecimal("450.00"),
        "MAD",
        "Pottery",
        null,
        now,
        now);
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor asArtisan(
      UUID userId) {
    return SecurityMockMvcRequestPostProcessors.authentication(
        new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority("ROLE_ARTISAN"))));
  }

  private static org.springframework.test.web.servlet.request.RequestPostProcessor asBuyer(
      UUID userId) {
    return SecurityMockMvcRequestPostProcessors.authentication(
        new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority("ROLE_BUYER"))));
  }

  @Test
  void createWithValidRequestReturnsCreated() throws Exception {
    UUID userId = UUID.randomUUID();
    when(createProductHandler.handle(any())).thenReturn(stubResult(UUID.randomUUID()));

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/products")
                .with(csrf())
                .with(asArtisan(userId))
                .contentType("application/json")
                .content(
                    """
                    {"name":"Zellige Tile Set","description":"Handmade","priceAmount":450.00,"priceCurrency":"MAD","craftType":"Pottery"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Zellige Tile Set"));
  }

  @Test
  void createRejectsBlankName() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/products")
                .with(csrf())
                .with(asArtisan(userId))
                .contentType("application/json")
                .content(
                    """
                    {"name":"","priceAmount":10.00,"priceCurrency":"MAD","craftType":"Pottery"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
  }

  @Test
  void createRejectsNonArtisanRole() throws Exception {
    UUID userId = UUID.randomUUID();
    when(createProductHandler.handle(any())).thenThrow(new NotAnArtisanException());

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/products")
                .with(csrf())
                .with(asBuyer(userId))
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new UpsertProductRequest(
                            "Name", null, new BigDecimal("10.00"), "MAD", "Craft", null))))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("NOT_AN_ARTISAN"));
  }

  @Test
  void updateReturnsNotFoundForSomeoneElsesProduct() throws Exception {
    UUID userId = UUID.randomUUID();
    when(updateProductHandler.handle(any())).thenThrow(new ProductNotFoundException());

    mockMvc
        .perform(
            put("/api/v1/artisan-profiles/me/products/" + UUID.randomUUID())
                .with(csrf())
                .with(asArtisan(userId))
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new UpsertProductRequest(
                            "Name", null, new BigDecimal("10.00"), "MAD", "Craft", null))))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("PRODUCT_NOT_FOUND"));
  }

  @Test
  void deleteReturnsNoContent() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            delete("/api/v1/artisan-profiles/me/products/" + UUID.randomUUID())
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isNoContent());
  }

  @Test
  void listReturnsOwnProducts() throws Exception {
    UUID userId = UUID.randomUUID();
    when(listOwnProductsHandler.handle(any())).thenReturn(List.of(stubResult(UUID.randomUUID())));

    mockMvc
        .perform(get("/api/v1/artisan-profiles/me/products").with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Zellige Tile Set"));
  }
}
