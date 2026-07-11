package ma.sana3.adapter.persistence.order;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataOrderItemRepository extends JpaRepository<OrderItemJpaEntity, UUID> {

  List<OrderItemJpaEntity> findByOrderId(UUID orderId);

  List<OrderItemJpaEntity> findByArtisanProfileId(UUID artisanProfileId);
}
