package ma.sana3.application.order;

import java.util.UUID;

public record GetMyOrderDetailQuery(UUID buyerUserId, UUID orderId) {}
