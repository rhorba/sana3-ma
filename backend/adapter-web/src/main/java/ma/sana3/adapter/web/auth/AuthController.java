package ma.sana3.adapter.web.auth;

import jakarta.validation.Valid;
import java.time.Duration;
import ma.sana3.application.auth.AuthResult;
import ma.sana3.application.auth.LoginCommand;
import ma.sana3.application.auth.LoginHandler;
import ma.sana3.application.auth.RefreshTokenCommand;
import ma.sana3.application.auth.RefreshTokenHandler;
import ma.sana3.application.auth.RegisterUserCommand;
import ma.sana3.application.auth.RegisterUserHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

  private static final String REFRESH_COOKIE_NAME = "refresh_token";

  private final RegisterUserHandler registerUserHandler;
  private final LoginHandler loginHandler;
  private final RefreshTokenHandler refreshTokenHandler;
  private final long refreshTokenTtlDays;

  AuthController(
      RegisterUserHandler registerUserHandler,
      LoginHandler loginHandler,
      RefreshTokenHandler refreshTokenHandler,
      @Value("${app.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays) {
    this.registerUserHandler = registerUserHandler;
    this.loginHandler = loginHandler;
    this.refreshTokenHandler = refreshTokenHandler;
    this.refreshTokenTtlDays = refreshTokenTtlDays;
  }

  @PostMapping("/register")
  ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    AuthResult result =
        registerUserHandler.handle(
            new RegisterUserCommand(
                request.email(), request.password(), request.role().toDomain()));
    return withRefreshCookie(result, HttpStatus.CREATED);
  }

  @PostMapping("/login")
  ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResult result = loginHandler.handle(new LoginCommand(request.email(), request.password()));
    return withRefreshCookie(result, HttpStatus.OK);
  }

  @PostMapping("/refresh")
  ResponseEntity<AuthResponse> refresh(@CookieValue(REFRESH_COOKIE_NAME) String refreshToken) {
    AuthResult result = refreshTokenHandler.handle(new RefreshTokenCommand(refreshToken));
    return withRefreshCookie(result, HttpStatus.OK);
  }

  // Stateless JWT: there is no server-side session/token store to revoke, so this only expires the
  // httpOnly refresh cookie. A refresh token already extracted from the cookie by other means would
  // still be valid until its natural TTL — accepted limitation of the stateless design (ADR-3).
  @PostMapping("/logout")
  ResponseEntity<Void> logout() {
    ResponseCookie expiredCookie =
        ResponseCookie.from(REFRESH_COOKIE_NAME, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/api/v1/auth")
            .maxAge(Duration.ZERO)
            .build();
    return ResponseEntity.noContent()
        .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
        .build();
  }

  private ResponseEntity<AuthResponse> withRefreshCookie(AuthResult result, HttpStatus status) {
    ResponseCookie cookie =
        ResponseCookie.from(REFRESH_COOKIE_NAME, result.refreshToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("Strict")
            .path("/api/v1/auth")
            .maxAge(Duration.ofDays(refreshTokenTtlDays))
            .build();
    AuthResponse body =
        new AuthResponse(
            result.userId(),
            result.email(),
            result.role(),
            result.accessToken(),
            result.accessTokenExpiresInSeconds());
    return ResponseEntity.status(status)
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(body);
  }
}
