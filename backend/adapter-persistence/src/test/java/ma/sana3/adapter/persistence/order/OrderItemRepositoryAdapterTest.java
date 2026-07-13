package ma.sana3.adapter.persistence.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.adapter.persistence.artisanprofile.ArtisanProfileJpaEntity;
import ma.sana3.adapter.persistence.catalog.ProductJpaEntity;
import ma.sana3.adapter.persistence.user.UserJpaEntity;
import ma.sana3.domain.order.Order;
import ma.sana3.domain.order.OrderItem;
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
@Import({OrderItemRepositoryAdapter.class, OrderRepositoryAdapter.class})
@Testcontainers
class OrderItemRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private OrderItemRepositoryAdapter repository;

  @Autowired private OrderRepositoryAdapter orderRepository;

  @Autowired private EntityManager entityManager;

  private UUID persistOrder() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    entityManager.persist(
        new UserJpaEntity(userId, userId + "@example.com", "hash", Role.BUYER, now, now));
    entityManager.flush();
    return orderRepository.save(Order.place(userId, "Address")).id();
  }

  private UUID persistProductWithArtisanProfile() {
    UUID artisanUserId = UUID.randomUUID();
    Instant now = Instant.now();
    entityManager.persist(
        new UserJpaEntity(
            artisanUserId, artisanUserId + "@example.com", "hash", Role.ARTISAN, now, now));
    UUID artisanProfileId = UUID.randomUUID();
    entityManager.persist(
        new ArtisanProfileJpaEntity(
            artisanProfileId, "Name", "Pottery", null, null, null, now, now));
    UUID productId = UUID.randomUUID();
    entityManager.persist(
        new ProductJpaEntity(
            productId,
            artisanProfileId,
            "Zellige Tile Set",
            null,
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            null,
            now,
            now));
    entityManager.flush();
    return productId;
  }

  @Test
  void savesAndFindsById() {
    UUID orderId = persistOrder();
    UUID productId = persistProductWithArtisanProfile();
    ProductJpaEntity product = entityManager.find(ProductJpaEntity.class, productId);
    OrderItem item =
        OrderItem.create(
            orderId,
            productId,
            "Zellige Tile Set",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            product.getArtisanProfileId(),
            2);

    repository.save(item);
    Optional<OrderItem> found = repository.findById(item.id());

    assertTrue(found.isPresent());
    assertEquals(2, found.get().quantity());
    assertEquals(new BigDecimal("900.00"), found.get().lineTotal());
  }

  @Test
  void findsByOrderId() {
    UUID orderId = persistOrder();
    UUID productId = persistProductWithArtisanProfile();
    ProductJpaEntity product = entityManager.find(ProductJpaEntity.class, productId);
    repository.save(
        OrderItem.create(
            orderId,
            productId,
            "Item 1",
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            product.getArtisanProfileId(),
            1));
    repository.save(
        OrderItem.create(
            orderId,
            productId,
            "Item 2",
            new BigDecimal("20.00"),
            "MAD",
            "Pottery",
            product.getArtisanProfileId(),
            1));

    List<OrderItem> found = repository.findByOrderId(orderId);

    assertEquals(2, found.size());
  }

  @Test
  void findsByArtisanProfileId() {
    UUID orderId = persistOrder();
    UUID productId = persistProductWithArtisanProfile();
    ProductJpaEntity product = entityManager.find(ProductJpaEntity.class, productId);
    repository.save(
        OrderItem.create(
            orderId,
            productId,
            "Item",
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            product.getArtisanProfileId(),
            1));

    List<OrderItem> found = repository.findByArtisanProfileId(product.getArtisanProfileId());

    assertEquals(1, found.size());
  }

  @Test
  void productIdSurvivesProductDeletion() {
    UUID orderId = persistOrder();
    UUID productId = persistProductWithArtisanProfile();
    ProductJpaEntity product = entityManager.find(ProductJpaEntity.class, productId);
    OrderItem saved =
        repository.save(
            OrderItem.create(
                orderId,
                productId,
                "Zellige Tile Set",
                new BigDecimal("450.00"),
                "MAD",
                "Pottery",
                product.getArtisanProfileId(),
                1));

    entityManager.remove(entityManager.find(ProductJpaEntity.class, productId));
    entityManager.flush();
    entityManager.clear();

    Optional<OrderItem> found = repository.findById(saved.id());
    assertTrue(found.isPresent());
    assertNull(found.get().productId());
    assertEquals("Zellige Tile Set", found.get().productNameSnapshot());
  }
}
