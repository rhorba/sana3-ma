package ma.sana3.application.auth;

import ma.sana3.domain.user.DuplicateEmailException;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserHandler {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final TokenService tokenService;

  public RegisterUserHandler(
      UserRepository userRepository, PasswordHasher passwordHasher, TokenService tokenService) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.tokenService = tokenService;
  }

  public AuthResult handle(RegisterUserCommand command) {
    String email = command.email().toLowerCase();
    if (userRepository.existsByEmail(email)) {
      throw new DuplicateEmailException(email);
    }

    String passwordHash = passwordHasher.hash(command.rawPassword());
    User user = userRepository.save(User.register(email, passwordHash, command.role()));

    TokenService.IssuedAccessToken accessToken =
        tokenService.generateAccessToken(user.id(), user.email(), user.role());
    String refreshToken = tokenService.generateRefreshToken(user.id());

    return new AuthResult(
        user.id(),
        user.email(),
        user.role(),
        accessToken.token(),
        accessToken.expiresInSeconds(),
        refreshToken);
  }
}
