package ma.sana3.adapter.persistence.order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

@Repository
class OrderRepositoryAdapter implements OrderRepository {

  private final SpringDataOrderRepository springDataOrderRepository;

  OrderRepositoryAdapter(SpringDataOrderRepository springDataOrderRepository) {
    this.springDataOrderRepository = springDataOrderRepository;
  }

  @Override
  public Order save(Order order) {
    OrderJpaEntity saved = springDataOrderRepository.save(OrderEntityMapper.toEntity(order));
    return OrderEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return springDataOrderRepository.findById(id).map(OrderEntityMapper::toDomain);
  }

  @Override
  public List<Order> findByBuyerUserId(UUID buyerUserId) {
    return springDataOrderRepository.findByBuyerUserId(buyerUserId).stream()
        .map(OrderEntityMapper::toDomain)
        .toList();
  }

  @Override
  public List<Order> findByIds(Collection<UUID> ids) {
    return springDataOrderRepository.findAllById(ids).stream()
        .map(OrderEntityMapper::toDomain)
        .toList();
  }
}
