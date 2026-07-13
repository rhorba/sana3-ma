package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoveMemberHandlerTest {

  @Mock private CooperativeMembershipRepository membershipRepository;

  private RemoveMemberHandler handler;

  @BeforeEach
  void setUp() {
    handler = new RemoveMemberHandler(membershipRepository);
  }

  @Test
  void ownerCanRemoveAMember() {
    UUID ownerId = UUID.randomUUID();
    UUID memberId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    CooperativeMembership owner =
        CooperativeMembership.create(ownerId, profileId, MembershipRole.OWNER);
    CooperativeMembership member =
        CooperativeMembership.create(memberId, profileId, MembershipRole.MEMBER);
    when(membershipRepository.findByUserId(ownerId)).thenReturn(Optional.of(owner));
    when(membershipRepository.findByUserId(memberId)).thenReturn(Optional.of(member));

    handler.handle(new RemoveMemberCommand(ownerId, Role.ARTISAN, memberId));

    verify(membershipRepository).delete(member);
  }

  @Test
  void memberCanRemoveThemselves() {
    UUID memberId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    CooperativeMembership member =
        CooperativeMembership.create(memberId, profileId, MembershipRole.MEMBER);
    when(membershipRepository.findByUserId(memberId)).thenReturn(Optional.of(member));

    handler.handle(new RemoveMemberCommand(memberId, Role.ARTISAN, memberId));

    verify(membershipRepository).delete(member);
  }

  @Test
  void nonOwnerCannotRemoveAnotherMember() {
    UUID memberId = UUID.randomUUID();
    UUID otherMemberId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    CooperativeMembership member =
        CooperativeMembership.create(memberId, profileId, MembershipRole.MEMBER);
    CooperativeMembership other =
        CooperativeMembership.create(otherMemberId, profileId, MembershipRole.MEMBER);
    when(membershipRepository.findByUserId(memberId)).thenReturn(Optional.of(member));
    when(membershipRepository.findByUserId(otherMemberId)).thenReturn(Optional.of(other));

    assertThrows(
        NotCooperativeOwnerException.class,
        () -> handler.handle(new RemoveMemberCommand(memberId, Role.ARTISAN, otherMemberId)));

    verify(membershipRepository, never()).delete(other);
  }

  @Test
  void cannotRemoveTheOwner() {
    UUID ownerId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    CooperativeMembership owner =
        CooperativeMembership.create(ownerId, profileId, MembershipRole.OWNER);
    when(membershipRepository.findByUserId(ownerId)).thenReturn(Optional.of(owner));

    assertThrows(
        CannotRemoveOwnerException.class,
        () -> handler.handle(new RemoveMemberCommand(ownerId, Role.ARTISAN, ownerId)));
  }

  @Test
  void rejectsRemovingSomeoneFromADifferentCooperative() {
    UUID ownerId = UUID.randomUUID();
    UUID otherUserId = UUID.randomUUID();
    CooperativeMembership owner =
        CooperativeMembership.create(ownerId, UUID.randomUUID(), MembershipRole.OWNER);
    CooperativeMembership otherCooperativeMember =
        CooperativeMembership.create(otherUserId, UUID.randomUUID(), MembershipRole.MEMBER);
    when(membershipRepository.findByUserId(ownerId)).thenReturn(Optional.of(owner));
    when(membershipRepository.findByUserId(otherUserId))
        .thenReturn(Optional.of(otherCooperativeMember));

    assertThrows(
        MemberNotFoundException.class,
        () -> handler.handle(new RemoveMemberCommand(ownerId, Role.ARTISAN, otherUserId)));
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();

    assertThrows(
        NotAnArtisanException.class,
        () -> handler.handle(new RemoveMemberCommand(userId, Role.BUYER, UUID.randomUUID())));
  }
}
