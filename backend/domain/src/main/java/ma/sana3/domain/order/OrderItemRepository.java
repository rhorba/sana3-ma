package ma.sana3.domain.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository {

  OrderItem save(OrderItem item);

  Optional<OrderItem> findById(UUID id);

  List<OrderItem> findByOrderId(UUID orderId);

  List<OrderItem> findByArtisanProfileId(UUID artisanProfileId);
}
