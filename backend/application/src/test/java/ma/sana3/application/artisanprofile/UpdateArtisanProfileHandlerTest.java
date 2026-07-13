package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
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
class UpdateArtisanProfileHandlerTest {

  @Mock private ArtisanProfileRepository artisanProfileRepository;

  @Mock private CooperativeMembershipRepository membershipRepository;

  private UpdateArtisanProfileHandler handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateArtisanProfileHandler(artisanProfileRepository, membershipRepository);
  }

  @Test
  void createsNewProfileAndOwnerMembershipWhenNoneExists() {
    UUID userId = UUID.randomUUID();
    UpdateArtisanProfileCommand command =
        new UpdateArtisanProfileCommand(
            userId, Role.ARTISAN, "Fatima Zahra", "Pottery", "Fes", "Bio", "+212600000000");
    when(membershipRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(artisanProfileRepository.save(any(ArtisanProfile.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ArtisanProfileResult result = handler.handle(command);

    assertEquals("Fatima Zahra", result.displayName());
    assertEquals("Pottery", result.craftType());
    verify(membershipRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                membership ->
                    membership.userId().equals(userId)
                        && membership.artisanProfileId().equals(result.id())
                        && membership.role() == MembershipRole.OWNER));
  }

  @Test
  void ownerUpdatesExistingProfileKeepingIdentity() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile existing = ArtisanProfile.create("Old Name", "Old Craft", null, null, null);
    UpdateArtisanProfileCommand command =
        new UpdateArtisanProfileCommand(
            userId, Role.ARTISAN, "New Name", "New Craft", "Rabat", "New bio", "+212611111111");
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(CooperativeMembership.create(userId, existing.id(), MembershipRole.OWNER)));
    when(artisanProfileRepository.findById(existing.id())).thenReturn(Optional.of(existing));
    when(artisanProfileRepository.save(any(ArtisanProfile.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ArtisanProfileResult result = handler.handle(command);

    assertEquals(existing.id(), result.id());
    assertEquals("New Name", result.displayName());
    assertEquals("Rabat", result.region());
  }

  @Test
  void memberCanUpdateSharedProfileTooNotJustOwner() {
    UUID memberId = UUID.randomUUID();
    ArtisanProfile existing = ArtisanProfile.create("Old Name", "Old Craft", null, null, null);
    UpdateArtisanProfileCommand command =
        new UpdateArtisanProfileCommand(
            memberId, Role.ARTISAN, "New Name", "New Craft", "Rabat", "New bio", "+212611111111");
    when(membershipRepository.findByUserId(memberId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(memberId, existing.id(), MembershipRole.MEMBER)));
    when(artisanProfileRepository.findById(existing.id())).thenReturn(Optional.of(existing));
    when(artisanProfileRepository.save(any(ArtisanProfile.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    ArtisanProfileResult result = handler.handle(command);

    assertEquals("New Name", result.displayName());
    verify(membershipRepository, never()).save(any());
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();
    UpdateArtisanProfileCommand command =
        new UpdateArtisanProfileCommand(userId, Role.BUYER, "Name", "Craft", null, null, null);

    assertThrows(NotAnArtisanException.class, () -> handler.handle(command));

    verify(artisanProfileRepository, never()).save(any());
  }
}
