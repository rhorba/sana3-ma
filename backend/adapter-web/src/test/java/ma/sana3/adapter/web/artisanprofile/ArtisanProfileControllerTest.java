package ma.sana3.adapter.web.artisanprofile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.artisanprofile.ArtisanProfileResult;
import ma.sana3.application.artisanprofile.GetArtisanProfileHandler;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.application.artisanprofile.UpdateArtisanProfileHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ArtisanProfileController.class)
class ArtisanProfileControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private UpdateArtisanProfileHandler updateArtisanProfileHandler;

  @MockitoBean private GetArtisanProfileHandler getArtisanProfileHandler;

  private static ArtisanProfileResult stubResult() {
    Instant now = Instant.now();
    return new ArtisanProfileResult(
        UUID.randomUUID(), "Fatima Zahra", "Pottery", "Fes", "Bio", "+212600000000", now, now);
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
  void upsertWithValidRequestReturnsOk() throws Exception {
    UUID userId = UUID.randomUUID();
    when(updateArtisanProfileHandler.handle(any())).thenReturn(stubResult());

    mockMvc
        .perform(
            put("/api/v1/artisan-profiles/me")
                .with(csrf())
                .with(asArtisan(userId))
                .contentType("application/json")
                .content(
                    """
                                {"displayName":"Fatima Zahra","craftType":"Pottery","region":"Fes","bio":"Bio","contactPhone":"+212600000000"}
                                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("Fatima Zahra"))
        .andExpect(jsonPath("$.craftType").value("Pottery"));
  }

  @Test
  void upsertRejectsBlankDisplayName() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            put("/api/v1/artisan-profiles/me")
                .with(csrf())
                .with(asArtisan(userId))
                .contentType("application/json")
                .content(
                    """
                                {"displayName":"","craftType":"Pottery"}
                                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
  }

  @Test
  void upsertRejectsNonArtisanRole() throws Exception {
    UUID userId = UUID.randomUUID();
    when(updateArtisanProfileHandler.handle(any())).thenThrow(new NotAnArtisanException());

    mockMvc
        .perform(
            put("/api/v1/artisan-profiles/me")
                .with(csrf())
                .with(asBuyer(userId))
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new UpsertArtisanProfileRequest("Name", "Craft", null, null, null))))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("NOT_AN_ARTISAN"));
  }

  @Test
  void getReturnsProfileWhenPresent() throws Exception {
    UUID userId = UUID.randomUUID();
    when(getArtisanProfileHandler.handle(any())).thenReturn(stubResult());

    mockMvc
        .perform(get("/api/v1/artisan-profiles/me").with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.displayName").value("Fatima Zahra"));
  }

  @Test
  void getReturnsNotFoundWhenProfileMissing() throws Exception {
    UUID userId = UUID.randomUUID();
    when(getArtisanProfileHandler.handle(any())).thenThrow(new ProfileNotFoundException());

    mockMvc
        .perform(get("/api/v1/artisan-profiles/me").with(asArtisan(userId)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("PROFILE_NOT_FOUND"));
  }
}
