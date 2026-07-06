package ma.sana3.adapter.web.auth;

import ma.sana3.domain.user.Role;

/**
 * Only roles selectable via public self-registration. ADMIN is intentionally excluded
 * so the role can never be trusted from client input (docs/stories-sana3-ma.md Story 1.1).
 */
public enum RegistrableRole {
    BUYER,
    ARTISAN;

    public Role toDomain() {
        return Role.valueOf(name());
    }
}
