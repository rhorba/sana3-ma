package ma.sana3.adapter.persistence.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import org.springframework.stereotype.Repository;

@Repository
class OrderItemRepositoryAdapter implements OrderItemRepository {

  private final SpringDataOrderItemRepository springDataOrderItemRepository;

  OrderItemRepositoryAdapter(SpringDataOrderItemRepository springDataOrderItemRepository) {
    this.springDataOrderItemRepository = springDataOrderItemRepository;
  }

  @Override
  public OrderItem save(OrderItem item) {
    OrderItemJpaEntity saved =
        springDataOrderItemRepository.save(OrderItemEntityMapper.toEntity(item));
    return OrderItemEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<OrderItem> findById(UUID id) {
    return springDataOrderItemRepository.findById(id).map(OrderItemEntityMapper::toDomain);
  }

  @Override
  public List<OrderItem> findByOrderId(UUID orderId) {
    return springDataOrderItemRepository.findByOrderId(orderId).stream()
        .map(OrderItemEntityMapper::toDomain)
        .toList();
  }

  @Override
  public List<OrderItem> findByArtisanProfileId(UUID artisanProfileId) {
    return springDataOrderItemRepository.findByArtisanProfileId(artisanProfileId).stream()
        .map(OrderItemEntityMapper::toDomain)
        .toList();
  }
}
