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
import ma.sana3.domain.catalog.ProductSearchCriteria;
import ma.sana3.domain.catalog.ProductSearchResult;
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
    return persistArtisanProfile("Pottery", null);
  }

  private UUID persistArtisanProfile(String craftType, String region) {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    entityManager.persist(
        new UserJpaEntity(userId, userId + "@example.com", "hash", Role.ARTISAN, now, now));
    UUID profileId = UUID.randomUUID();
    entityManager.persist(
        new ArtisanProfileJpaEntity(
            profileId, userId, "Name", craftType, region, null, null, now, now));
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

  @Test
  void searchFiltersByCraftType() {
    UUID potteryArtisan = persistArtisanProfile("Pottery", "Fes");
    UUID weavingArtisan = persistArtisanProfile("Weaving", "Fes");
    repository.save(
        Product.create(
            potteryArtisan, "Tile", null, new BigDecimal("10.00"), "MAD", "Pottery", null));
    repository.save(
        Product.create(
            weavingArtisan, "Rug", null, new BigDecimal("10.00"), "MAD", "Weaving", null));

    ProductSearchResult result =
        repository.search(new ProductSearchCriteria("pottery", null, null, null, null, 0, 20));

    assertEquals(1, result.products().size());
    assertEquals("Tile", result.products().get(0).name());
  }

  @Test
  void searchFiltersByArtisanRegion() {
    UUID fesArtisan = persistArtisanProfile("Pottery", "Fes");
    UUID rabatArtisan = persistArtisanProfile("Pottery", "Rabat");
    repository.save(
        Product.create(
            fesArtisan, "Fes Tile", null, new BigDecimal("10.00"), "MAD", "Pottery", null));
    repository.save(
        Product.create(
            rabatArtisan, "Rabat Tile", null, new BigDecimal("10.00"), "MAD", "Pottery", null));

    ProductSearchResult result =
        repository.search(new ProductSearchCriteria(null, "Rabat", null, null, null, 0, 20));

    assertEquals(1, result.products().size());
    assertEquals("Rabat Tile", result.products().get(0).name());
  }

  @Test
  void searchFiltersByPriceRange() {
    UUID artisanProfileId = persistArtisanProfile();
    repository.save(
        Product.create(
            artisanProfileId, "Cheap", null, new BigDecimal("10.00"), "MAD", "Pottery", null));
    repository.save(
        Product.create(
            artisanProfileId, "Expensive", null, new BigDecimal("900.00"), "MAD", "Pottery", null));

    ProductSearchResult result =
        repository.search(
            new ProductSearchCriteria(
                null, null, new BigDecimal("100.00"), new BigDecimal("1000.00"), null, 0, 20));

    assertEquals(1, result.products().size());
    assertEquals("Expensive", result.products().get(0).name());
  }

  @Test
  void searchFiltersByKeywordMatchingNameOrDescription() {
    UUID artisanProfileId = persistArtisanProfile();
    repository.save(
        Product.create(
            artisanProfileId,
            "Zellige Tile Set",
            "Handmade blue tiles",
            new BigDecimal("10.00"),
            "MAD",
            "Pottery",
            null));
    repository.save(
        Product.create(
            artisanProfileId,
            "Leather Bag",
            null,
            new BigDecimal("10.00"),
            "MAD",
            "Leather",
            null));

    ProductSearchResult result =
        repository.search(new ProductSearchCriteria(null, null, null, null, "zellige", 0, 20));

    assertEquals(1, result.products().size());
    assertEquals("Zellige Tile Set", result.products().get(0).name());
  }

  @Test
  void searchPaginatesResults() {
    UUID artisanProfileId = persistArtisanProfile();
    for (int i = 0; i < 3; i++) {
      repository.save(
          Product.create(
              artisanProfileId,
              "Product " + i,
              null,
              new BigDecimal("10.00"),
              "MAD",
              "Pottery",
              null));
    }

    ProductSearchResult firstPage =
        repository.search(new ProductSearchCriteria(null, null, null, null, null, 0, 2));
    ProductSearchResult secondPage =
        repository.search(new ProductSearchCriteria(null, null, null, null, null, 1, 2));

    assertEquals(2, firstPage.products().size());
    assertEquals(3, firstPage.totalElements());
    assertEquals(1, secondPage.products().size());
  }
}
