package ma.sana3.adapter.web.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PlaceOrderRequest(
    @NotBlank @Size(max = 500) String shippingAddress,
    @NotEmpty List<@Valid OrderLineItemRequest> items) {}
