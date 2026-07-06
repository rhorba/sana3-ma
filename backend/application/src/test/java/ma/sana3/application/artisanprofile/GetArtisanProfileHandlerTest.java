package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetArtisanProfileHandlerTest {

    @Mock
    private ArtisanProfileRepository artisanProfileRepository;

    private GetArtisanProfileHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetArtisanProfileHandler(artisanProfileRepository);
    }

    @Test
    void returnsExistingProfile() {
        UUID userId = UUID.randomUUID();
        ArtisanProfile profile = ArtisanProfile.create(userId, "Name", "Craft", "Region", "Bio", "+212600000000");
        when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        ArtisanProfileResult result = handler.handle(new GetArtisanProfileQuery(userId));

        assertEquals(profile.id(), result.id());
        assertEquals("Name", result.displayName());
    }

    @Test
    void throwsWhenProfileMissing() {
        UUID userId = UUID.randomUUID();
        when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ProfileNotFoundException.class, () -> handler.handle(new GetArtisanProfileQuery(userId)));
    }
}
