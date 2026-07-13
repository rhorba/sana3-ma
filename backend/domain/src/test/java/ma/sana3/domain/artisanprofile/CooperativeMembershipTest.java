package ma.sana3.domain.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CooperativeMembershipTest {

  @Test
  void createBuildsOwnerMembership() {
    UUID userId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();

    CooperativeMembership membership =
        CooperativeMembership.create(userId, profileId, MembershipRole.OWNER);

    assertEquals(userId, membership.userId());
    assertEquals(profileId, membership.artisanProfileId());
    assertEquals(MembershipRole.OWNER, membership.role());
    assertTrue(membership.isOwner());
    assertNotNull(membership.id());
    assertNotNull(membership.joinedAt());
  }

  @Test
  void createBuildsMemberMembership() {
    CooperativeMembership membership =
        CooperativeMembership.create(UUID.randomUUID(), UUID.randomUUID(), MembershipRole.MEMBER);

    assertFalse(membership.isOwner());
  }

  @Test
  void constructorRejectsNullUserId() {
    assertThrows(
        NullPointerException.class,
        () ->
            new CooperativeMembership(
                UUID.randomUUID(), null, UUID.randomUUID(), MembershipRole.OWNER, Instant.now()));
  }

  @Test
  void equalityIsByIdOnly() {
    UUID id = UUID.randomUUID();
    CooperativeMembership a =
        new CooperativeMembership(
            id, UUID.randomUUID(), UUID.randomUUID(), MembershipRole.OWNER, Instant.now());
    CooperativeMembership b =
        new CooperativeMembership(
            id, UUID.randomUUID(), UUID.randomUUID(), MembershipRole.MEMBER, Instant.now());

    assertEquals(a, b);
  }
}
