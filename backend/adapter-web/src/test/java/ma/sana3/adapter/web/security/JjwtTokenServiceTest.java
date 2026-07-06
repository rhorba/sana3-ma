package ma.sana3.adapter.web.security;

import ma.sana3.application.auth.InvalidTokenException;
import ma.sana3.application.auth.TokenService;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JjwtTokenServiceTest {

    private static final String SECRET = "test-only-secret-key-must-be-at-least-32-bytes-long";

    private JjwtTokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new JjwtTokenService(SECRET, 15, 7);
    }

    @Test
    void accessTokenRoundTripsToOriginalClaims() {
        UUID userId = UUID.randomUUID();
        TokenService.IssuedAccessToken issued = tokenService.generateAccessToken(userId, "user@example.com", Role.ARTISAN);

        JjwtTokenService.AccessTokenClaims claims = tokenService.parseAccessToken(issued.token());

        assertEquals(userId, claims.userId());
        assertEquals("user@example.com", claims.email());
        assertEquals(Role.ARTISAN, claims.role());
        assertEquals(15 * 60, issued.expiresInSeconds());
    }

    @Test
    void refreshTokenRoundTripsToUserId() {
        UUID userId = UUID.randomUUID();
        String refreshToken = tokenService.generateRefreshToken(userId);

        assertEquals(userId, tokenService.parseRefreshToken(refreshToken));
    }

    @Test
    void parseRefreshTokenRejectsAccessToken() {
        UUID userId = UUID.randomUUID();
        String accessToken = tokenService.generateAccessToken(userId, "user@example.com", Role.BUYER).token();

        assertThrows(InvalidTokenException.class, () -> tokenService.parseRefreshToken(accessToken));
    }

    @Test
    void parseAccessTokenRejectsRefreshToken() {
        UUID userId = UUID.randomUUID();
        String refreshToken = tokenService.generateRefreshToken(userId);

        assertThrows(InvalidTokenException.class, () -> tokenService.parseAccessToken(refreshToken));
    }

    @Test
    void parseAccessTokenRejectsGarbage() {
        assertThrows(InvalidTokenException.class, () -> tokenService.parseAccessToken("not-a-jwt"));
    }
}
