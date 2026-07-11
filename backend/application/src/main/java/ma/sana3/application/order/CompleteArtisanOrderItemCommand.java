package ma.sana3.application.order;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record CompleteArtisanOrderItemCommand(UUID userId, Role userRole, UUID orderItemId) {}
