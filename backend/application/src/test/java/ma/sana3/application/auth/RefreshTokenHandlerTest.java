package ma.sana3.application.auth;

import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenHandlerTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;

    private RefreshTokenHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RefreshTokenHandler(userRepository, tokenService);
    }

    @Test
    void issuesNewTokensForValidRefreshToken() {
        User user = User.register("a@b.com", "hashed", Role.BUYER);
        when(tokenService.parseRefreshToken("old-refresh")).thenReturn(user.id());
        when(userRepository.findById(user.id())).thenReturn(Optional.of(user));
        when(tokenService.generateAccessToken(any(), any(), any()))
                .thenReturn(new TokenService.IssuedAccessToken("new-access", 900));
        when(tokenService.generateRefreshToken(any())).thenReturn("new-refresh");

        AuthResult result = handler.handle(new RefreshTokenCommand("old-refresh"));

        assertEquals("new-access", result.accessToken());
        assertEquals("new-refresh", result.refreshToken());
    }

    @Test
    void rejectsTokenForDeletedUser() {
        UUID userId = UUID.randomUUID();
        when(tokenService.parseRefreshToken("stale-refresh")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> handler.handle(new RefreshTokenCommand("stale-refresh")));
    }
}
