package ma.sana3.adapter.persistence.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.adapter.persistence.artisanprofile.ArtisanProfileJpaEntity;
import ma.sana3.adapter.persistence.user.UserJpaEntity;
import ma.sana3.domain.catalog.Product;
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
@Import(ProductRepositoryAdapter.class)
@Testcontainers
class ProductRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private ProductRepositoryAdapter repository;

  @Autowired private EntityManager entityManager;

  private UUID persistArtisanProfile() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    entityManager.persist(
        new UserJpaEntity(userId, userId + "@example.com", "hash", Role.ARTISAN, now, now));
    UUID profileId = UUID.randomUUID();
    entityManager.persist(
        new ArtisanProfileJpaEntity(
            profileId, userId, "Name", "Pottery", null, null, null, now, now));
    entityManager.flush();
    return profileId;
  }

  @Test
  void savesAndFindsById() {
    UUID artisanProfileId = persistArtisanProfile();
    Product product =
        Product.create(
            artisanProfileId,
            "Zellige Tile Set",
            "Handmade",
            new BigDecimal("450.00"),
            "MAD",
            "Pottery",
            null);

    repository.save(product);
    Optional<Product> found = repository.findById(product.id());

    assertTrue(found.isPresent());
    assertEquals("Zellige Tile Set", found.get().name());
    assertEquals(new BigDecimal("450.00"), found.get().priceAmount());
  }

  @Test
  void findsByArtisanProfileId() {
    UUID artisanProfileId = persistArtisanProfile();
    repository.save(
        Product.create(
            artisanProfileId, "Product 1", null, new BigDecimal("10.00"), "MAD", "Pottery", null));
    repository.save(
        Product.create(
            artisanProfileId, "Product 2", null, new BigDecimal("20.00"), "MAD", "Pottery", null));

    List<Product> found = repository.findByArtisanProfileId(artisanProfileId);

    assertEquals(2, found.size());
  }

  @Test
  void deletesById() {
    UUID artisanProfileId = persistArtisanProfile();
    Product saved =
        repository.save(
            Product.create(
                artisanProfileId, "Name", null, new BigDecimal("10.00"), "MAD", "Pottery", null));

    repository.deleteById(saved.id());

    assertTrue(repository.findById(saved.id()).isEmpty());
  }

  @Test
  void findByIdReturnsEmptyForUnknownId() {
    assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
  }
}
