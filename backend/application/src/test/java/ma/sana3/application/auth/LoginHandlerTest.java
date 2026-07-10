package ma.sana3.application.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordHasher passwordHasher;
  @Mock private TokenService tokenService;

  private LoginHandler handler;

  @BeforeEach
  void setUp() {
    handler = new LoginHandler(userRepository, passwordHasher, tokenService);
  }

  @Test
  void issuesTokensForCorrectCredentials() {
    User user = User.register("a@b.com", "hashed", Role.ARTISAN);
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
    when(passwordHasher.matches("password123", "hashed")).thenReturn(true);
    when(tokenService.generateAccessToken(any(), any(), any()))
        .thenReturn(new TokenService.IssuedAccessToken("access-token", 900));
    when(tokenService.generateRefreshToken(any())).thenReturn("refresh-token");

    AuthResult result = handler.handle(new LoginCommand("a@b.com", "password123"));

    assertEquals(user.id(), result.userId());
    assertEquals("access-token", result.accessToken());
  }

  @Test
  void rejectsUnknownEmail() {
    when(userRepository.findByEmail("missing@b.com")).thenReturn(Optional.empty());

    assertThrows(
        InvalidCredentialsException.class,
        () -> handler.handle(new LoginCommand("missing@b.com", "password123")));
  }

  @Test
  void rejectsWrongPassword() {
    User user = User.register("a@b.com", "hashed", Role.ARTISAN);
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
    when(passwordHasher.matches("wrong-password", "hashed")).thenReturn(false);

    assertThrows(
        InvalidCredentialsException.class,
        () -> handler.handle(new LoginCommand("a@b.com", "wrong-password")));
  }
}
