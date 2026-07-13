package ma.sana3.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemRepository;
import ma.sana3.domain.order.OrderRepository;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListArtisanOrderItemsHandlerTest {

  @Mock private CooperativeMembershipRepository membershipRepository;
  @Mock private OrderItemRepository orderItemRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private UserRepository userRepository;

  private ListArtisanOrderItemsHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new ListArtisanOrderItemsHandler(
            membershipRepository, orderItemRepository, orderRepository, userRepository);
  }

  @Test
  void listsOrderItemsForTheArtisansOwnProfileWithBuyerAndShippingInfo() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Order order = Order.place(UUID.randomUUID(), "123 Rue Example, Fes");
    OrderItem item =
        OrderItem.create(
            order.id(),
            UUID.randomUUID(),
            "Tile",
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            artisanProfileId,
            1);
    User buyer =
        new User(
            order.buyerUserId(),
            "buyer@example.com",
            "hash",
            Role.BUYER,
            Instant.now(),
            Instant.now());
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.MEMBER)));
    when(orderItemRepository.findByArtisanProfileId(artisanProfileId)).thenReturn(List.of(item));
    when(orderRepository.findByIds(List.of(order.id()))).thenReturn(List.of(order));
    when(userRepository.findByIds(List.of(order.buyerUserId()))).thenReturn(List.of(buyer));

    List<ArtisanOrderItemResult> results =
        handler.handle(new ListArtisanOrderItemsQuery(userId, Role.ARTISAN));

    assertEquals(1, results.size());
    assertEquals("buyer@example.com", results.get(0).buyerEmail());
    assertEquals("123 Rue Example, Fes", results.get(0).shippingAddress());
  }

  @Test
  void returnsEmptyListWhenTheArtisanHasNoOrderItems() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.OWNER)));
    when(orderItemRepository.findByArtisanProfileId(artisanProfileId)).thenReturn(List.of());

    List<ArtisanOrderItemResult> results =
        handler.handle(new ListArtisanOrderItemsQuery(userId, Role.ARTISAN));

    assertEquals(0, results.size());
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();

    assertThrows(
        NotAnArtisanException.class,
        () -> handler.handle(new ListArtisanOrderItemsQuery(userId, Role.BUYER)));
  }

  @Test
  void rejectsAnArtisanWithoutMembership() {
    UUID userId = UUID.randomUUID();
    when(membershipRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        ProfileNotFoundException.class,
        () -> handler.handle(new ListArtisanOrderItemsQuery(userId, Role.ARTISAN)));
  }
}
