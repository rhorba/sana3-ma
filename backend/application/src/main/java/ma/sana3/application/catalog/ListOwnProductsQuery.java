package ma.sana3.application.catalog;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record ListOwnProductsQuery(UUID userId, Role userRole) {}
