package ma.sana3.application.auth;

import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginHandler {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;

    public LoginHandler(UserRepository userRepository, PasswordHasher passwordHasher, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenService = tokenService;
    }

    public AuthResult handle(LoginCommand command) {
        User user = userRepository.findByEmail(command.email().toLowerCase())
                .filter(u -> passwordHasher.matches(command.rawPassword(), u.passwordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        TokenService.IssuedAccessToken accessToken = tokenService.generateAccessToken(user.id(), user.email(), user.role());
        String refreshToken = tokenService.generateRefreshToken(user.id());

        return new AuthResult(user.id(), user.email(), user.role(), accessToken.token(), accessToken.expiresInSeconds(), refreshToken);
    }
}
