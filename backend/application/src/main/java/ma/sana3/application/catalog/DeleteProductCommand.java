package ma.sana3.application.catalog;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record DeleteProductCommand(UUID userId, Role userRole, UUID productId) {}
