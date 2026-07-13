package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InviteMemberHandlerTest {

  @Mock private CooperativeMembershipRepository membershipRepository;
  @Mock private CooperativeInviteRepository inviteRepository;
  @Mock private UserRepository userRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private InviteMemberHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new InviteMemberHandler(
            membershipRepository, inviteRepository, userRepository, artisanProfileRepository);
  }

  private User artisanUser(String email) {
    return new User(UUID.randomUUID(), email, "hash", Role.ARTISAN, Instant.now(), Instant.now());
  }

  @Test
  void ownerInvitesEligibleArtisanSuccessfully() {
    UUID ownerId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    User invitee = artisanUser("invitee@example.com");
    InviteMemberCommand command =
        new InviteMemberCommand(ownerId, Role.ARTISAN, "invitee@example.com");
    when(membershipRepository.findByUserId(ownerId))
        .thenReturn(
            Optional.of(CooperativeMembership.create(ownerId, profileId, MembershipRole.OWNER)));
    when(userRepository.findByEmail("invitee@example.com")).thenReturn(Optional.of(invitee));
    when(membershipRepository.existsByUserId(invitee.id())).thenReturn(false);
    when(inviteRepository.existsPendingByInvitedUserId(invitee.id())).thenReturn(false);
    when(inviteRepository.save(any(CooperativeInvite.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(artisanProfileRepository.findById(profileId))
        .thenReturn(Optional.of(ArtisanProfile.create("Coop", "Weaving", null, null, null)));

    CooperativeInviteResult result = handler.handle(command);

    assertEquals(profileId, result.artisanProfileId());
    assertEquals(invitee.id(), captureInvitedUserId());
  }

  private UUID captureInvitedUserId() {
    var captor = org.mockito.ArgumentCaptor.forClass(CooperativeInvite.class);
    verify(inviteRepository).save(captor.capture());
    return captor.getValue().invitedUserId();
  }

  @Test
  void rejectsNonOwnerMember() {
    UUID memberId = UUID.randomUUID();
    InviteMemberCommand command =
        new InviteMemberCommand(memberId, Role.ARTISAN, "invitee@example.com");
    when(membershipRepository.findByUserId(memberId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(memberId, UUID.randomUUID(), MembershipRole.MEMBER)));

    assertThrows(NotCooperativeOwnerException.class, () -> handler.handle(command));

    verify(inviteRepository, never()).save(any());
  }

  @Test
  void rejectsNonArtisanRole() {
    InviteMemberCommand command =
        new InviteMemberCommand(UUID.randomUUID(), Role.BUYER, "invitee@example.com");

    assertThrows(NotAnArtisanException.class, () -> handler.handle(command));
  }

  @Test
  void rejectsWhenInviteeDoesNotExistOrIsNotArtisan() {
    UUID ownerId = UUID.randomUUID();
    InviteMemberCommand command =
        new InviteMemberCommand(ownerId, Role.ARTISAN, "nobody@example.com");
    when(membershipRepository.findByUserId(ownerId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(ownerId, UUID.randomUUID(), MembershipRole.OWNER)));
    when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

    assertThrows(InviteeNotEligibleException.class, () -> handler.handle(command));
  }

  @Test
  void rejectsInviteeAlreadyInACooperative() {
    UUID ownerId = UUID.randomUUID();
    User invitee = artisanUser("invitee@example.com");
    InviteMemberCommand command =
        new InviteMemberCommand(ownerId, Role.ARTISAN, "invitee@example.com");
    when(membershipRepository.findByUserId(ownerId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(ownerId, UUID.randomUUID(), MembershipRole.OWNER)));
    when(userRepository.findByEmail("invitee@example.com")).thenReturn(Optional.of(invitee));
    when(membershipRepository.existsByUserId(invitee.id())).thenReturn(true);

    assertThrows(InviteeAlreadyMemberException.class, () -> handler.handle(command));
  }

  @Test
  void rejectsInviteeWithAnExistingPendingInvite() {
    UUID ownerId = UUID.randomUUID();
    User invitee = artisanUser("invitee@example.com");
    InviteMemberCommand command =
        new InviteMemberCommand(ownerId, Role.ARTISAN, "invitee@example.com");
    when(membershipRepository.findByUserId(ownerId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(ownerId, UUID.randomUUID(), MembershipRole.OWNER)));
    when(userRepository.findByEmail("invitee@example.com")).thenReturn(Optional.of(invitee));
    when(membershipRepository.existsByUserId(invitee.id())).thenReturn(false);
    when(inviteRepository.existsPendingByInvitedUserId(invitee.id())).thenReturn(true);

    assertThrows(InviteeAlreadyInvitedException.class, () -> handler.handle(command));
  }
}
