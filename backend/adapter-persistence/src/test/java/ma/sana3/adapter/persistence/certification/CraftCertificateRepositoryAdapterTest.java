package ma.sana3.adapter.persistence.certification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.adapter.persistence.artisanprofile.ArtisanProfileJpaEntity;
import ma.sana3.adapter.persistence.catalog.ProductJpaEntity;
import ma.sana3.domain.certification.CraftCertificate;
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
@Import(CraftCertificateRepositoryAdapter.class)
@Testcontainers
class CraftCertificateRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private CraftCertificateRepositoryAdapter repository;

  @Autowired private EntityManager entityManager;

  private record ProductRef(UUID productId, UUID artisanProfileId) {}

  private ProductRef persistProduct() {
    Instant now = Instant.now();
    UUID profileId = UUID.randomUUID();
    entityManager.persist(
        new ArtisanProfileJpaEntity(profileId, "Coop", "Weaving", null, null, null, now, now));
    UUID productId = UUID.randomUUID();
    entityManager.persist(
        new ProductJpaEntity(
            productId,
            profileId,
            "Rug",
            null,
            new BigDecimal("100.00"),
            "MAD",
            "Weaving",
            null,
            now,
            now));
    entityManager.flush();
    return new ProductRef(productId, profileId);
  }

  @Test
  void savesAndFindsById() {
    ProductRef product = persistProduct();
    CraftCertificate saved =
        repository.save(CraftCertificate.issue(product.productId(), product.artisanProfileId()));

    Optional<CraftCertificate> found = repository.findById(saved.id());

    assertTrue(found.isPresent());
    assertEquals(product.productId(), found.get().productId());
  }

  @Test
  void findsByProductId() {
    ProductRef product = persistProduct();
    CraftCertificate saved =
        repository.save(CraftCertificate.issue(product.productId(), product.artisanProfileId()));

    Optional<CraftCertificate> found = repository.findByProductId(product.productId());

    assertTrue(found.isPresent());
    assertEquals(saved.id(), found.get().id());
  }

  @Test
  void findByProductIdReturnsEmptyWhenNoneIssued() {
    ProductRef product = persistProduct();

    assertTrue(repository.findByProductId(product.productId()).isEmpty());
  }

  @Test
  void findByIdReturnsEmptyForUnknownId() {
    assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
  }
}
