package ma.sana3.adapter.web.auth;

import ma.sana3.domain.user.Role;

import java.util.UUID;

public record AuthResponse(UUID userId, String email, Role role, String accessToken, long expiresInSeconds) {
}
