package ma.sana3.application.order;

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
public class CompleteArtisanOrderItemHandler {

  private final ArtisanProfileRepository artisanProfileRepository;
  private final OrderItemRepository orderItemRepository;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

  public CompleteArtisanOrderItemHandler(
      ArtisanProfileRepository artisanProfileRepository,
      OrderItemRepository orderItemRepository,
      OrderRepository orderRepository,
      UserRepository userRepository) {
    this.artisanProfileRepository = artisanProfileRepository;
    this.orderItemRepository = orderItemRepository;
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
  }

  public ArtisanOrderItemResult handle(CompleteArtisanOrderItemCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    ArtisanProfile profile =
        artisanProfileRepository
            .findByUserId(command.userId())
            .orElseThrow(ProfileNotFoundException::new);

    OrderItem item =
        orderItemRepository
            .findById(command.orderItemId())
            .filter(existing -> existing.artisanProfileId().equals(profile.id()))
            .orElseThrow(OrderItemNotFoundException::new);

    OrderItem completed = orderItemRepository.save(item.complete());
    Order order =
        orderRepository.findById(completed.orderId()).orElseThrow(OrderNotFoundException::new);
    User buyer =
        userRepository.findById(order.buyerUserId()).orElseThrow(OrderNotFoundException::new);

    return ArtisanOrderItemResultMapper.toResult(completed, order, buyer);
  }
}
