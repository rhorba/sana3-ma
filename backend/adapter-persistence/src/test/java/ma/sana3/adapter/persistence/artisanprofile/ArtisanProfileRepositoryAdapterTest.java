package ma.sana3.adapter.persistence.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
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
@Import(ArtisanProfileRepositoryAdapter.class)
@Testcontainers
class ArtisanProfileRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private ArtisanProfileRepositoryAdapter repository;

  @Test
  void savesAndFindsById() {
    ArtisanProfile saved =
        repository.save(
            ArtisanProfile.create("Fatima Zahra", "Pottery", "Fes", "Bio", "+212600000000"));

    Optional<ArtisanProfile> found = repository.findById(saved.id());

    assertTrue(found.isPresent());
    assertEquals("Fatima Zahra", found.get().displayName());
    assertEquals("Pottery", found.get().craftType());
  }

  @Test
  void savingWithSameIdUpdatesExistingProfile() {
    ArtisanProfile original =
        repository.save(ArtisanProfile.create("Old Name", "Old Craft", null, null, null));

    ArtisanProfile updated =
        original.withDetails("New Name", "New Craft", "Rabat", "New bio", "+212611111111");
    repository.save(updated);

    Optional<ArtisanProfile> found = repository.findById(original.id());
    assertTrue(found.isPresent());
    assertEquals(original.id(), found.get().id());
    assertEquals("New Name", found.get().displayName());
  }

  @Test
  void findByIdReturnsEmptyForUnknownId() {
    assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
  }

  @Test
  void findsByIds() {
    ArtisanProfile profile1 =
        repository.save(ArtisanProfile.create("Name 1", "Pottery", null, null, null));
    ArtisanProfile profile2 =
        repository.save(ArtisanProfile.create("Name 2", "Weaving", null, null, null));

    List<ArtisanProfile> found =
        repository.findByIds(List.of(profile1.id(), profile2.id(), UUID.randomUUID()));

    assertEquals(2, found.size());
  }
}
