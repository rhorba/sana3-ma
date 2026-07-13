package ma.sana3.application.order;

import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import ma.sana3.domain.order.OrderStatus;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CompleteArtisanOrderItemHandler {

  private final CooperativeMembershipRepository membershipRepository;
  private final OrderItemRepository orderItemRepository;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

  public CompleteArtisanOrderItemHandler(
      CooperativeMembershipRepository membershipRepository,
      OrderItemRepository orderItemRepository,
      OrderRepository orderRepository,
      UserRepository userRepository) {
    this.membershipRepository = membershipRepository;
    this.orderItemRepository = orderItemRepository;
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
  }

  public ArtisanOrderItemResult handle(CompleteArtisanOrderItemCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    var artisanProfileId =
        membershipRepository
            .findByUserId(command.userId())
            .map(CooperativeMembership::artisanProfileId)
            .orElseThrow(ProfileNotFoundException::new);

    OrderItem item =
        orderItemRepository
            .findById(command.orderItemId())
            .filter(existing -> existing.artisanProfileId().equals(artisanProfileId))
            .orElseThrow(OrderItemNotFoundException::new);

    Order order = orderRepository.findById(item.orderId()).orElseThrow(OrderNotFoundException::new);
    if (order.status() == OrderStatus.CANCELLED) {
      throw new OrderCancelledException();
    }

    OrderItem completed = orderItemRepository.save(item.complete());
    User buyer =
        userRepository.findById(order.buyerUserId()).orElseThrow(OrderNotFoundException::new);

    return ArtisanOrderItemResultMapper.toResult(completed, order, buyer);
  }
}
