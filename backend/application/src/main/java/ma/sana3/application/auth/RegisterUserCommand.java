package ma.sana3.application.auth;

import ma.sana3.domain.user.Role;

public record RegisterUserCommand(String email, String rawPassword, Role role) {
}
