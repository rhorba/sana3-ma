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
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.MembershipRole;
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
@Import({CooperativeMembershipRepositoryAdapter.class, ArtisanProfileRepositoryAdapter.class})
@Testcontainers
class CooperativeMembershipRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private CooperativeMembershipRepositoryAdapter membershipRepository;

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
            ArtisanProfile.create("Cooperative", "Weaving", "Fes", null, null));
    return profile.id();
  }

  @Test
  void savesAndFindsByUserId() {
    UUID ownerId = persistUser();
    UUID profileId = persistProfile();
    UUID memberId = persistUser();

    membershipRepository.save(
        CooperativeMembership.create(ownerId, profileId, MembershipRole.OWNER));
    membershipRepository.save(
        CooperativeMembership.create(memberId, profileId, MembershipRole.MEMBER));

    Optional<CooperativeMembership> found = membershipRepository.findByUserId(memberId);
    assertTrue(found.isPresent());
    assertEquals(MembershipRole.MEMBER, found.get().role());
    assertEquals(profileId, found.get().artisanProfileId());
  }

  @Test
  void findByUserIdReturnsEmptyForUnknownUser() {
    assertTrue(membershipRepository.findByUserId(UUID.randomUUID()).isEmpty());
  }

  @Test
  void findsAllMembersOfAProfile() {
    UUID ownerId = persistUser();
    UUID profileId = persistProfile();
    UUID memberId = persistUser();
    membershipRepository.save(
        CooperativeMembership.create(ownerId, profileId, MembershipRole.OWNER));
    membershipRepository.save(
        CooperativeMembership.create(memberId, profileId, MembershipRole.MEMBER));

    List<CooperativeMembership> members = membershipRepository.findByArtisanProfileId(profileId);

    assertEquals(2, members.size());
  }

  @Test
  void existsByUserIdReflectsMembership() {
    UUID ownerId = persistUser();
    UUID profileId = persistProfile();
    assertFalse(membershipRepository.existsByUserId(ownerId));

    membershipRepository.save(
        CooperativeMembership.create(ownerId, profileId, MembershipRole.OWNER));

    assertTrue(membershipRepository.existsByUserId(ownerId));
  }

  @Test
  void deleteRemovesMembership() {
    UUID ownerId = persistUser();
    UUID profileId = persistProfile();
    CooperativeMembership saved =
        membershipRepository.save(
            CooperativeMembership.create(ownerId, profileId, MembershipRole.OWNER));

    membershipRepository.delete(saved);

    assertTrue(membershipRepository.findByUserId(ownerId).isEmpty());
  }
}
