package ma.sana3.application.auth;

import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshTokenHandler {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public RefreshTokenHandler(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public AuthResult handle(RefreshTokenCommand command) {
        UUID userId = tokenService.parseRefreshToken(command.refreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException("User for refresh token no longer exists"));

        TokenService.IssuedAccessToken accessToken = tokenService.generateAccessToken(user.id(), user.email(), user.role());
        String refreshToken = tokenService.generateRefreshToken(user.id());

        return new AuthResult(user.id(), user.email(), user.role(), accessToken.token(), accessToken.expiresInSeconds(), refreshToken);
    }
}
