package ma.sana3.application.order;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ListArtisanOrderItemsHandler {

  private final ArtisanProfileRepository artisanProfileRepository;
  private final OrderItemRepository orderItemRepository;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

  public ListArtisanOrderItemsHandler(
      ArtisanProfileRepository artisanProfileRepository,
      OrderItemRepository orderItemRepository,
      OrderRepository orderRepository,
      UserRepository userRepository) {
    this.artisanProfileRepository = artisanProfileRepository;
    this.orderItemRepository = orderItemRepository;
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
  }

  public List<ArtisanOrderItemResult> handle(ListArtisanOrderItemsQuery query) {
    if (query.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    ArtisanProfile profile =
        artisanProfileRepository
            .findByUserId(query.userId())
            .orElseThrow(ProfileNotFoundException::new);

    List<OrderItem> items = orderItemRepository.findByArtisanProfileId(profile.id());
    if (items.isEmpty()) {
      return List.of();
    }

    Map<UUID, Order> ordersById =
        orderRepository
            .findByIds(items.stream().map(OrderItem::orderId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(Order::id, Function.identity()));
    Map<UUID, User> buyersById =
        userRepository
            .findByIds(ordersById.values().stream().map(Order::buyerUserId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(User::id, Function.identity()));

    return items.stream()
        .map(
            item -> {
              Order order = ordersById.get(item.orderId());
              return ArtisanOrderItemResultMapper.toResult(
                  item, order, buyersById.get(order.buyerUserId()));
            })
        .toList();
  }
}
