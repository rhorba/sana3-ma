package ma.sana3.application.order;

import java.util.UUID;

public record CancelMyOrderCommand(UUID buyerUserId, UUID orderId) {}
