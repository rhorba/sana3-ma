package ma.sana3.application.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
import ma.sana3.domain.order.OrderItemAlreadyCompletedException;
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
class CompleteArtisanOrderItemHandlerTest {

  @Mock private ArtisanProfileRepository artisanProfileRepository;
  @Mock private OrderItemRepository orderItemRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private UserRepository userRepository;

  private CompleteArtisanOrderItemHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new CompleteArtisanOrderItemHandler(
            artisanProfileRepository, orderItemRepository, orderRepository, userRepository);
  }

  @Test
  void completesTheArtisansOwnOrderItem() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    Order order = Order.place(UUID.randomUUID(), "Address");
    OrderItem item =
        OrderItem.create(
            order.id(),
            UUID.randomUUID(),
            "Tile",
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            profile.id(),
            1);
    User buyer =
        new User(
            order.buyerUserId(),
            "buyer@example.com",
            "hash",
            Role.BUYER,
            Instant.now(),
            Instant.now());
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(orderItemRepository.findById(item.id())).thenReturn(Optional.of(item));
    when(orderItemRepository.save(any(OrderItem.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(orderRepository.findById(order.id())).thenReturn(Optional.of(order));
    when(userRepository.findById(order.buyerUserId())).thenReturn(Optional.of(buyer));

    ArtisanOrderItemResult result =
        handler.handle(new CompleteArtisanOrderItemCommand(userId, Role.ARTISAN, item.id()));

    assertTrue(result.completed());
    assertEquals("buyer@example.com", result.buyerEmail());
  }

  @Test
  void rejectsAnOrderItemBelongingToAnotherArtisan() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    OrderItem item =
        OrderItem.create(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "Tile",
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            UUID.randomUUID(),
            1);
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(orderItemRepository.findById(item.id())).thenReturn(Optional.of(item));

    assertThrows(
        OrderItemNotFoundException.class,
        () -> handler.handle(new CompleteArtisanOrderItemCommand(userId, Role.ARTISAN, item.id())));
  }

  @Test
  void rejectsAnAlreadyCompletedItem() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Pottery", null, null, null);
    OrderItem completed =
        OrderItem.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tile",
                new BigDecimal("10.00"),
                "MAD",
                "Pottery",
                profile.id(),
                1)
            .complete();
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(orderItemRepository.findById(completed.id())).thenReturn(Optional.of(completed));

    assertThrows(
        OrderItemAlreadyCompletedException.class,
        () ->
            handler.handle(
                new CompleteArtisanOrderItemCommand(userId, Role.ARTISAN, completed.id())));
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();

    assertThrows(
        NotAnArtisanException.class,
        () ->
            handler.handle(
                new CompleteArtisanOrderItemCommand(userId, Role.BUYER, UUID.randomUUID())));
  }

  @Test
  void rejectsAnArtisanWithoutAProfile() {
    UUID userId = UUID.randomUUID();
    when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        ProfileNotFoundException.class,
        () ->
            handler.handle(
                new CompleteArtisanOrderItemCommand(userId, Role.ARTISAN, UUID.randomUUID())));
  }
}
