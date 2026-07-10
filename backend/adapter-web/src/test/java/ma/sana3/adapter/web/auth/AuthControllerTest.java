package ma.sana3.adapter.web.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import ma.sana3.application.auth.AuthResult;
import ma.sana3.application.auth.InvalidCredentialsException;
import ma.sana3.application.auth.InvalidTokenException;
import ma.sana3.application.auth.LoginHandler;
import ma.sana3.application.auth.RefreshTokenHandler;
import ma.sana3.application.auth.RegisterUserHandler;
import ma.sana3.domain.user.DuplicateEmailException;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "app.jwt.refresh-token-ttl-days=7")
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean private RegisterUserHandler registerUserHandler;

  @MockitoBean private LoginHandler loginHandler;

  @MockitoBean private RefreshTokenHandler refreshTokenHandler;

  private static AuthResult stubResult() {
    return new AuthResult(
        UUID.randomUUID(), "user@example.com", Role.BUYER, "access-token", 900, "refresh-token");
  }

  @Test
  void registerWithValidRequestReturnsCreatedWithRefreshCookie() throws Exception {
    when(registerUserHandler.handle(any())).thenReturn(stubResult());

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new RegisterRequest(
                            "user@example.com", "password123", RegistrableRole.BUYER))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").value("access-token"))
        .andExpect(jsonPath("$.email").value("user@example.com"))
        .andExpect(cookie().exists("refresh_token"))
        .andExpect(cookie().httpOnly("refresh_token", true));
  }

  @Test
  void registerRejectsAdminRole() throws Exception {
    String bodyWithAdminRole =
        """
                {"email":"user@example.com","password":"password123","role":"ADMIN"}
                """;

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType("application/json")
                .content(bodyWithAdminRole))
        .andExpect(status().isBadRequest());
  }

  @Test
  void registerRejectsTooShortPassword() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new RegisterRequest("user@example.com", "short", RegistrableRole.BUYER))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"));
  }

  @Test
  void registerWithDuplicateEmailReturnsConflict() throws Exception {
    when(registerUserHandler.handle(any()))
        .thenThrow(new DuplicateEmailException("user@example.com"));

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new RegisterRequest(
                            "user@example.com", "password123", RegistrableRole.BUYER))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("EMAIL_ALREADY_REGISTERED"));
  }

  @Test
  void loginWithWrongCredentialsReturnsUnauthorized() throws Exception {
    when(loginHandler.handle(any())).thenThrow(new InvalidCredentialsException());

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new LoginRequest("user@example.com", "wrong-password"))))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void loginWithValidCredentialsReturnsOk() throws Exception {
    when(loginHandler.handle(any())).thenReturn(stubResult());

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType("application/json")
                .content(
                    objectMapper.writeValueAsString(
                        new LoginRequest("user@example.com", "password123"))))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("refresh_token"));
  }

  @Test
  void refreshWithInvalidTokenReturnsUnauthorized() throws Exception {
    when(refreshTokenHandler.handle(any()))
        .thenThrow(new InvalidTokenException("Invalid or expired token"));

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", "bad-token")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void refreshWithValidCookieReturnsNewAccessToken() throws Exception {
    when(refreshTokenHandler.handle(any())).thenReturn(stubResult());

    mockMvc
        .perform(
            post("/api/v1/auth/refresh")
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", "old-refresh")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access-token"))
        .andExpect(cookie().value("refresh_token", "refresh-token"));
  }

  @Test
  void logoutExpiresTheRefreshCookie() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/logout")
                .cookie(new jakarta.servlet.http.Cookie("refresh_token", "some-refresh-token")))
        .andExpect(status().isNoContent())
        .andExpect(cookie().maxAge("refresh_token", 0))
        .andExpect(cookie().httpOnly("refresh_token", true));
  }

  @Test
  void logoutWithoutAnExistingCookieStillSucceeds() throws Exception {
    mockMvc.perform(post("/api/v1/auth/logout")).andExpect(status().isNoContent());
  }
}
