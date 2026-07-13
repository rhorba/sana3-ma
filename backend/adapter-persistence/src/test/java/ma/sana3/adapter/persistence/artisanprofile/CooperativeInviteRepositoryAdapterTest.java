package ma.sana3.adapter.persistence.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.adapter.persistence.user.UserJpaEntity;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.InviteStatus;
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
@Import({CooperativeInviteRepositoryAdapter.class, ArtisanProfileRepositoryAdapter.class})
@Testcontainers
class CooperativeInviteRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private CooperativeInviteRepositoryAdapter inviteRepository;

  @Autowired private ArtisanProfileRepositoryAdapter artisanProfileRepository;

  @Autowired private EntityManager entityManager;

  private UUID persistUser() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    entityManager.persist(
        new UserJpaEntity(userId, userId + "@example.com", "hash", Role.ARTISAN, now, now));
    entityManager.flush();
    return userId;
  }

  private UUID persistProfile() {
    ArtisanProfile profile =
        artisanProfileRepository.save(
            ArtisanProfile.create("Cooperative", "Weaving", null, null, null));
    return profile.id();
  }

  @Test
  void savesAndFindsById() {
    UUID profileId = persistProfile();
    UUID inviteeId = persistUser();
    CooperativeInvite saved = inviteRepository.save(CooperativeInvite.create(profileId, inviteeId));

    Optional<CooperativeInvite> found = inviteRepository.findById(saved.id());

    assertTrue(found.isPresent());
    assertEquals(InviteStatus.PENDING, found.get().status());
  }

  @Test
  void findsPendingInvitesByInvitedUserId() {
    UUID profileId = persistProfile();
    UUID inviteeId = persistUser();
    inviteRepository.save(CooperativeInvite.create(profileId, inviteeId));
    inviteRepository.save(CooperativeInvite.create(profileId, inviteeId).decline());

    List<CooperativeInvite> pending = inviteRepository.findPendingByInvitedUserId(inviteeId);

    assertEquals(1, pending.size());
  }

  @Test
  void existsPendingByInvitedUserIdReflectsState() {
    UUID profileId = persistProfile();
    UUID inviteeId = persistUser();
    assertFalse(inviteRepository.existsPendingByInvitedUserId(inviteeId));

    inviteRepository.save(CooperativeInvite.create(profileId, inviteeId));

    assertTrue(inviteRepository.existsPendingByInvitedUserId(inviteeId));
  }

  @Test
  void savingAcceptedInviteUpdatesStatus() {
    UUID profileId = persistProfile();
    UUID inviteeId = persistUser();
    CooperativeInvite saved = inviteRepository.save(CooperativeInvite.create(profileId, inviteeId));

    inviteRepository.save(saved.accept());

    Optional<CooperativeInvite> found = inviteRepository.findById(saved.id());
    assertTrue(found.isPresent());
    assertEquals(InviteStatus.ACCEPTED, found.get().status());
  }
}
