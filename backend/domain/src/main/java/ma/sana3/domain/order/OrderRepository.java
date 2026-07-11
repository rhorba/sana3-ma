package ma.sana3.domain.order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  Order save(Order order);

  Optional<Order> findById(UUID id);

  List<Order> findByBuyerUserId(UUID buyerUserId);

  List<Order> findByIds(Collection<UUID> ids);
}
