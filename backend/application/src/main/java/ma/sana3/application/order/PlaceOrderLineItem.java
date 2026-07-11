package ma.sana3.application.order;

import java.util.UUID;

public record PlaceOrderLineItem(UUID productId, int quantity) {}
