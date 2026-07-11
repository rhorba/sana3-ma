package ma.sana3.adapter.persistence.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.adapter.persistence.user.UserJpaEntity;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrderRepositoryAdapter.class)
@Testcontainers
class OrderRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private OrderRepositoryAdapter repository;

  @Autowired private EntityManager entityManager;

  private UUID persistBuyer() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    entityManager.persist(
        new UserJpaEntity(userId, userId + "@example.com", "hash", Role.BUYER, now, now));
    entityManager.flush();
    return userId;
  }

  @Test
  void savesAndFindsById() {
    UUID buyerUserId = persistBuyer();
    Order order = Order.place(buyerUserId, "123 Rue Example, Fes");

    repository.save(order);
    Optional<Order> found = repository.findById(order.id());

    assertTrue(found.isPresent());
    assertEquals("123 Rue Example, Fes", found.get().shippingAddress());
    assertEquals(buyerUserId, found.get().buyerUserId());
  }

  @Test
  void findByIdReturnsEmptyForUnknownId() {
    assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
  }

  @Test
  void findsByBuyerUserId() {
    UUID buyerUserId = persistBuyer();
    repository.save(Order.place(buyerUserId, "Address 1"));
    repository.save(Order.place(buyerUserId, "Address 2"));

    List<Order> found = repository.findByBuyerUserId(buyerUserId);

    assertEquals(2, found.size());
  }

  @Test
  void savePersistsStatusTransitions() {
    UUID buyerUserId = persistBuyer();
    Order order = repository.save(Order.place(buyerUserId, "Address"));

    repository.save(order.cancel());
    Optional<Order> found = repository.findById(order.id());

    assertTrue(found.isPresent());
    assertEquals("CANCELLED", found.get().status().name());
  }
}
