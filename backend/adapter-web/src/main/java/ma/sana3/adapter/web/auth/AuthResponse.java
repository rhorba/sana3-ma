package ma.sana3.adapter.web.auth;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record AuthResponse(
    UUID userId, String email, Role role, String accessToken, long expiresInSeconds) {}
