package ma.sana3.adapter.web.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OrderLineItemRequest(@NotNull UUID productId, @Min(1) int quantity) {}
