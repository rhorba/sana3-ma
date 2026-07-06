package ma.sana3.adapter.persistence.artisanprofile;

import jakarta.persistence.EntityManager;
import ma.sana3.adapter.persistence.user.UserJpaEntity;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ArtisanProfileRepositoryAdapter.class)
@Testcontainers
class ArtisanProfileRepositoryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:16-3.4-alpine").asCompatibleSubstituteFor("postgres"));

    @Autowired
    private ArtisanProfileRepositoryAdapter repository;

    @Autowired
    private EntityManager entityManager;

    private UUID persistUser() {
        UUID userId = UUID.randomUUID();
        Instant now = Instant.now();
        entityManager.persist(new UserJpaEntity(
                userId, userId + "@example.com", "hash", Role.ARTISAN, now, now));
        entityManager.flush();
        return userId;
    }

    @Test
    void savesAndFindsByUserId() {
        UUID userId = persistUser();
        ArtisanProfile profile = ArtisanProfile.create(userId, "Fatima Zahra", "Pottery", "Fes", "Bio", "+212600000000");

        repository.save(profile);
        Optional<ArtisanProfile> found = repository.findByUserId(userId);

        assertTrue(found.isPresent());
        assertEquals("Fatima Zahra", found.get().displayName());
        assertEquals("Pottery", found.get().craftType());
    }

    @Test
    void savingWithSameIdUpdatesExistingProfile() {
        UUID userId = persistUser();
        ArtisanProfile original = repository.save(ArtisanProfile.create(userId, "Old Name", "Old Craft", null, null, null));

        ArtisanProfile updated = original.withDetails("New Name", "New Craft", "Rabat", "New bio", "+212611111111");
        repository.save(updated);

        Optional<ArtisanProfile> found = repository.findByUserId(userId);
        assertTrue(found.isPresent());
        assertEquals(original.id(), found.get().id());
        assertEquals("New Name", found.get().displayName());
    }

    @Test
    void findByUserIdReturnsEmptyForUnknownUser() {
        assertTrue(repository.findByUserId(UUID.randomUUID()).isEmpty());
    }
}
