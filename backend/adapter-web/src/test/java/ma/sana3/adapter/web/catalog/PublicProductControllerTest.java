package ma.sana3.adapter.web.catalog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.catalog.GetProductDetailHandler;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.application.catalog.PublicProductPage;
import ma.sana3.application.catalog.PublicProductSummary;
import ma.sana3.application.catalog.SearchProductsHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// SecurityConfig is package-private in ma.sana3.adapter.web.security, so this slice can't import
// it to exercise the real permitAll rule -- filters are disabled here to test controller/handler
// wiring in isolation. The actual "GET /api/v1/products is public" security rule is verified by a
// live smoke test against the running app instead (see .logs/activity.md Batch 13).
@WebMvcTest(controllers = PublicProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicProductControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private SearchProductsHandler searchProductsHandler;

  @MockitoBean private GetProductDetailHandler getProductDetailHandler;

  private static PublicProductSummary stubSummary() {
    return new PublicProductSummary(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "Zellige Tile Set",
        "Handmade",
        new BigDecimal("450.00"),
        "MAD",
        "Pottery",
        null,
        "Fatima Zahra",
        "Pottery",
        "Fes");
  }

  @Test
  void searchReturnsProductsWithArtisanSummary() throws Exception {
    when(searchProductsHandler.handle(any()))
        .thenReturn(new PublicProductPage(List.of(stubSummary()), 1, 0, 20));

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.products[0].name").value("Zellige Tile Set"))
        .andExpect(jsonPath("$.products[0].artisan.displayName").value("Fatima Zahra"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void searchNeverExposesArtisanContactPhoneOrEmail() throws Exception {
    when(searchProductsHandler.handle(any()))
        .thenReturn(new PublicProductPage(List.of(stubSummary()), 1, 0, 20));

    mockMvc
        .perform(get("/api/v1/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.products[0].artisan.contactPhone").doesNotExist())
        .andExpect(jsonPath("$.products[0].artisan.email").doesNotExist());
  }

  @Test
  void detailReturnsProduct() throws Exception {
    UUID productId = UUID.randomUUID();
    when(getProductDetailHandler.handle(any())).thenReturn(stubSummary());

    mockMvc
        .perform(get("/api/v1/products/" + productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Zellige Tile Set"));
  }

  @Test
  void detailReturnsNotFoundForUnknownProduct() throws Exception {
    UUID productId = UUID.randomUUID();
    when(getProductDetailHandler.handle(any())).thenThrow(new ProductNotFoundException());

    mockMvc
        .perform(get("/api/v1/products/" + productId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("PRODUCT_NOT_FOUND"));
  }
}
