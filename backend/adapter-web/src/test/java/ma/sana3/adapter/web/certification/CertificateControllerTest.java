package ma.sana3.adapter.web.certification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.application.certification.CertificateResult;
import ma.sana3.application.certification.IssueCertificateHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = CertificateController.class)
class CertificateControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private IssueCertificateHandler issueCertificateHandler;

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

  @Test
  void issueReturnsTheCertificate() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    when(issueCertificateHandler.handle(any()))
        .thenReturn(new CertificateResult(UUID.randomUUID(), productId, Instant.now()));

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/products/" + productId + "/certificate")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(productId.toString()));
  }

  @Test
  void issueRejectsNonArtisanRole() throws Exception {
    UUID userId = UUID.randomUUID();
    when(issueCertificateHandler.handle(any())).thenThrow(new NotAnArtisanException());

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/products/" + UUID.randomUUID() + "/certificate")
                .with(csrf())
                .with(asBuyer(userId)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("NOT_AN_ARTISAN"));
  }

  @Test
  void issueReturnsNotFoundForSomeoneElsesProduct() throws Exception {
    UUID userId = UUID.randomUUID();
    when(issueCertificateHandler.handle(any())).thenThrow(new ProductNotFoundException());

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/products/" + UUID.randomUUID() + "/certificate")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("PRODUCT_NOT_FOUND"));
  }
}
