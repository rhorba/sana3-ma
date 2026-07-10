package ma.sana3.application.auth;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record AuthResult(
    UUID userId,
    String email,
    Role role,
    String accessToken,
    long accessTokenExpiresInSeconds,
    String refreshToken) {}
