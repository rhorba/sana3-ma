package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListMyInvitesHandlerTest {

  @Mock private CooperativeInviteRepository inviteRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private ListMyInvitesHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ListMyInvitesHandler(inviteRepository, artisanProfileRepository);
  }

  @Test
  void returnsPendingInvitesWithArtisanDisplayName() {
    UUID userId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    CooperativeInvite invite = CooperativeInvite.create(profileId, userId);
    when(inviteRepository.findPendingByInvitedUserId(userId)).thenReturn(List.of(invite));
    when(artisanProfileRepository.findById(profileId))
        .thenReturn(Optional.of(ArtisanProfile.create("Coop Name", "Weaving", null, null, null)));

    List<CooperativeInviteResult> results = handler.handle(new ListMyInvitesQuery(userId));

    assertEquals(1, results.size());
    assertEquals("Coop Name", results.get(0).artisanDisplayName());
  }
}
