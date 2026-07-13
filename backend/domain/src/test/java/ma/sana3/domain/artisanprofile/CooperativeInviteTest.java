package ma.sana3.domain.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CooperativeInviteTest {

  @Test
  void createBuildsPendingInvite() {
    UUID profileId = UUID.randomUUID();
    UUID invitedUserId = UUID.randomUUID();

    CooperativeInvite invite = CooperativeInvite.create(profileId, invitedUserId);

    assertEquals(profileId, invite.artisanProfileId());
    assertEquals(invitedUserId, invite.invitedUserId());
    assertEquals(InviteStatus.PENDING, invite.status());
    assertNotNull(invite.id());
    assertNotNull(invite.createdAt());
    assertNull(invite.resolvedAt());
  }

  @Test
  void acceptTransitionsFromPending() {
    CooperativeInvite invite = CooperativeInvite.create(UUID.randomUUID(), UUID.randomUUID());

    CooperativeInvite accepted = invite.accept();

    assertEquals(InviteStatus.ACCEPTED, accepted.status());
    assertNotNull(accepted.resolvedAt());
  }

  @Test
  void declineTransitionsFromPending() {
    CooperativeInvite invite = CooperativeInvite.create(UUID.randomUUID(), UUID.randomUUID());

    CooperativeInvite declined = invite.decline();

    assertEquals(InviteStatus.DECLINED, declined.status());
  }

  @Test
  void acceptRejectsAlreadyAcceptedInvite() {
    CooperativeInvite accepted =
        CooperativeInvite.create(UUID.randomUUID(), UUID.randomUUID()).accept();

    assertThrows(IllegalInviteStatusTransitionException.class, accepted::accept);
  }

  @Test
  void declineRejectsAlreadyDeclinedInvite() {
    CooperativeInvite declined =
        CooperativeInvite.create(UUID.randomUUID(), UUID.randomUUID()).decline();

    assertThrows(IllegalInviteStatusTransitionException.class, declined::decline);
  }
}
