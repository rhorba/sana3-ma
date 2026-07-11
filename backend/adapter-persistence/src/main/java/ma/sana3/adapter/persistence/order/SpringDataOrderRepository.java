package ma.sana3.adapter.persistence.order;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {

  List<OrderJpaEntity> findByBuyerUserId(UUID buyerUserId);
}
