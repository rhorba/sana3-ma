package ma.sana3.adapter.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import ma.sana3.application.auth.InvalidTokenException;
import ma.sana3.application.auth.TokenService;
import ma.sana3.domain.user.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class JjwtTokenService implements TokenService {

  private static final String CLAIM_TYPE = "type";
  private static final String TYPE_ACCESS = "access";
  private static final String TYPE_REFRESH = "refresh";

  private final SecretKey key;
  private final Duration accessTokenTtl;
  private final Duration refreshTokenTtl;

  JjwtTokenService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes,
      @Value("${app.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    this.refreshTokenTtl = Duration.ofDays(refreshTokenTtlDays);
  }

  @Override
  public IssuedAccessToken generateAccessToken(UUID userId, String email, Role role) {
    Instant now = Instant.now();
    String token =
        Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role.name())
            .claim(CLAIM_TYPE, TYPE_ACCESS)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(accessTokenTtl)))
            .signWith(key)
            .compact();
    return new IssuedAccessToken(token, accessTokenTtl.toSeconds());
  }

  @Override
  public String generateRefreshToken(UUID userId) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(userId.toString())
        .claim(CLAIM_TYPE, TYPE_REFRESH)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(refreshTokenTtl)))
        .signWith(key)
        .compact();
  }

  @Override
  public UUID parseRefreshToken(String refreshToken) {
    Claims claims = parseClaims(refreshToken);
    if (!TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class))) {
      throw new InvalidTokenException("Token is not a refresh token");
    }
    return UUID.fromString(claims.getSubject());
  }

  AccessTokenClaims parseAccessToken(String accessToken) {
    Claims claims = parseClaims(accessToken);
    if (!TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class))) {
      throw new InvalidTokenException("Token is not an access token");
    }
    return new AccessTokenClaims(
        UUID.fromString(claims.getSubject()),
        claims.get("email", String.class),
        Role.valueOf(claims.get("role", String.class)));
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidTokenException("Invalid or expired token");
    }
  }

  record AccessTokenClaims(UUID userId, String email, Role role) {}
}
