package ma.sana3.application.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.sana3.domain.user.DuplicateEmailException;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserHandlerTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordHasher passwordHasher;
  @Mock private TokenService tokenService;

  private RegisterUserHandler handler;

  @BeforeEach
  void setUp() {
    handler = new RegisterUserHandler(userRepository, passwordHasher, tokenService);
  }

  @Test
  void registersNewUserAndIssuesTokens() {
    RegisterUserCommand command =
        new RegisterUserCommand("New@Example.com", "password123", Role.BUYER);
    when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(passwordHasher.hash("password123")).thenReturn("hashed");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(tokenService.generateAccessToken(any(), any(), any()))
        .thenReturn(new TokenService.IssuedAccessToken("access-token", 900));
    when(tokenService.generateRefreshToken(any())).thenReturn("refresh-token");

    AuthResult result = handler.handle(command);

    assertEquals("new@example.com", result.email());
    assertEquals(Role.BUYER, result.role());
    assertEquals("access-token", result.accessToken());
    assertEquals("refresh-token", result.refreshToken());
    assertEquals(900, result.accessTokenExpiresInSeconds());
  }

  @Test
  void rejectsDuplicateEmail() {
    RegisterUserCommand command =
        new RegisterUserCommand("dup@example.com", "password123", Role.BUYER);
    when(userRepository.existsByEmail("dup@example.com")).thenReturn(true);

    assertThrows(DuplicateEmailException.class, () -> handler.handle(command));

    verify(userRepository, never()).save(any());
  }
}
