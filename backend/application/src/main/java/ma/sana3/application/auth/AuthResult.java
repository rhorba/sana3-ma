package ma.sana3.application.auth;

import ma.sana3.domain.user.Role;

import java.util.UUID;

public record AuthResult(
        UUID userId,
        String email,
        Role role,
        String accessToken,
        long accessTokenExpiresInSeconds,
        String refreshToken
) {
}
