package ma.sana3.application.order;

import java.util.List;
import java.util.UUID;

public record PlaceOrderCommand(
    UUID buyerUserId, String shippingAddress, List<PlaceOrderLineItem> items) {}
