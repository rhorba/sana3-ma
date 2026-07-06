package ma.sana3.adapter.web.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email, @NotBlank String password) {
}
