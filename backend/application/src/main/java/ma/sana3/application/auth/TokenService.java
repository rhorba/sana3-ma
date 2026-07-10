package ma.sana3.application.auth;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public interface TokenService {

  IssuedAccessToken generateAccessToken(UUID userId, String email, Role role);

  String generateRefreshToken(UUID userId);

  /**
   * @throws InvalidTokenException if the token is malformed, expired, or not a refresh token
   */
  UUID parseRefreshToken(String refreshToken);

  record IssuedAccessToken(String token, long expiresInSeconds) {}
}
