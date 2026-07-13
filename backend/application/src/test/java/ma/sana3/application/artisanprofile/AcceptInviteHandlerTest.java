package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.InviteStatus;
import ma.sana3.domain.artisanprofile.MembershipRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcceptInviteHandlerTest {

  @Mock private CooperativeInviteRepository inviteRepository;
  @Mock private CooperativeMembershipRepository membershipRepository;

  private AcceptInviteHandler handler;

  @BeforeEach
  void setUp() {
    handler = new AcceptInviteHandler(inviteRepository, membershipRepository);
  }

  @Test
  void acceptingCreatesMemberMembership() {
    UUID userId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    CooperativeInvite invite = CooperativeInvite.create(profileId, userId);
    when(inviteRepository.findById(invite.id())).thenReturn(Optional.of(invite));
    when(membershipRepository.existsByUserId(userId)).thenReturn(false);
    when(inviteRepository.save(any(CooperativeInvite.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(membershipRepository.save(any(CooperativeMembership.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CooperativeInviteResult result =
        handler.handle(new RespondToInviteCommand(userId, invite.id()));

    assertEquals(InviteStatus.ACCEPTED, result.status());
    org.mockito.Mockito.verify(membershipRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                membership ->
                    membership.userId().equals(userId)
                        && membership.artisanProfileId().equals(profileId)
                        && membership.role() == MembershipRole.MEMBER));
  }

  @Test
  void rejectsInviteBelongingToAnotherUser() {
    UUID userId = UUID.randomUUID();
    CooperativeInvite invite = CooperativeInvite.create(UUID.randomUUID(), UUID.randomUUID());
    when(inviteRepository.findById(invite.id())).thenReturn(Optional.of(invite));

    assertThrows(
        InviteNotFoundException.class,
        () -> handler.handle(new RespondToInviteCommand(userId, invite.id())));
  }

  @Test
  void rejectsAlreadyAcceptedInvite() {
    UUID userId = UUID.randomUUID();
    CooperativeInvite invite = CooperativeInvite.create(UUID.randomUUID(), userId).accept();
    when(inviteRepository.findById(invite.id())).thenReturn(Optional.of(invite));
    when(membershipRepository.existsByUserId(userId)).thenReturn(false);

    assertThrows(
        ma.sana3.domain.artisanprofile.IllegalInviteStatusTransitionException.class,
        () -> handler.handle(new RespondToInviteCommand(userId, invite.id())));
  }

  @Test
  void rejectsWhenAlreadyAMember() {
    UUID userId = UUID.randomUUID();
    CooperativeInvite invite = CooperativeInvite.create(UUID.randomUUID(), userId);
    when(inviteRepository.findById(invite.id())).thenReturn(Optional.of(invite));
    when(membershipRepository.existsByUserId(userId)).thenReturn(true);

    assertThrows(
        InviteeAlreadyMemberException.class,
        () -> handler.handle(new RespondToInviteCommand(userId, invite.id())));
  }
}
