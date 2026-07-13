package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetArtisanProfileHandlerTest {

  @Mock private ArtisanProfileRepository artisanProfileRepository;

  @Mock private CooperativeMembershipRepository membershipRepository;

  private GetArtisanProfileHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetArtisanProfileHandler(artisanProfileRepository, membershipRepository);
  }

  @Test
  void returnsExistingProfile() {
    UUID userId = UUID.randomUUID();
    ArtisanProfile profile =
        ArtisanProfile.create("Name", "Craft", "Region", "Bio", "+212600000000");
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(CooperativeMembership.create(userId, profile.id(), MembershipRole.OWNER)));
    when(artisanProfileRepository.findById(profile.id())).thenReturn(Optional.of(profile));

    ArtisanProfileResult result = handler.handle(new GetArtisanProfileQuery(userId));

    assertEquals(profile.id(), result.id());
    assertEquals("Name", result.displayName());
  }

  @Test
  void throwsWhenNoMembership() {
    UUID userId = UUID.randomUUID();
    when(membershipRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        ProfileNotFoundException.class, () -> handler.handle(new GetArtisanProfileQuery(userId)));
  }
}
