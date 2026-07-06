package ma.sana3.application.auth;

import ma.sana3.domain.user.Role;

import java.util.UUID;

public interface TokenService {

    IssuedAccessToken generateAccessToken(UUID userId, String email, Role role);

    String generateRefreshToken(UUID userId);

    /**
     * @throws InvalidTokenException if the token is malformed, expired, or not a refresh token
     */
    UUID parseRefreshToken(String refreshToken);

    record IssuedAccessToken(String token, long expiresInSeconds) {
    }
}
