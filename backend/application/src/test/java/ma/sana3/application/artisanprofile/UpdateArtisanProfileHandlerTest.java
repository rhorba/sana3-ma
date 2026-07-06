package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateArtisanProfileHandlerTest {

    @Mock
    private ArtisanProfileRepository artisanProfileRepository;

    private UpdateArtisanProfileHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdateArtisanProfileHandler(artisanProfileRepository);
    }

    @Test
    void createsNewProfileWhenNoneExists() {
        UUID userId = UUID.randomUUID();
        UpdateArtisanProfileCommand command = new UpdateArtisanProfileCommand(
                userId, Role.ARTISAN, "Fatima Zahra", "Pottery", "Fes", "Bio", "+212600000000");
        when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(artisanProfileRepository.save(any(ArtisanProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArtisanProfileResult result = handler.handle(command);

        assertEquals(userId, result.userId());
        assertEquals("Fatima Zahra", result.displayName());
        assertEquals("Pottery", result.craftType());
    }

    @Test
    void updatesExistingProfileKeepingIdentity() {
        UUID userId = UUID.randomUUID();
        ArtisanProfile existing = ArtisanProfile.create(userId, "Old Name", "Old Craft", null, null, null);
        UpdateArtisanProfileCommand command = new UpdateArtisanProfileCommand(
                userId, Role.ARTISAN, "New Name", "New Craft", "Rabat", "New bio", "+212611111111");
        when(artisanProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(artisanProfileRepository.save(any(ArtisanProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArtisanProfileResult result = handler.handle(command);

        assertEquals(existing.id(), result.id());
        assertEquals("New Name", result.displayName());
        assertEquals("Rabat", result.region());
    }

    @Test
    void rejectsNonArtisanRole() {
        UUID userId = UUID.randomUUID();
        UpdateArtisanProfileCommand command = new UpdateArtisanProfileCommand(
                userId, Role.BUYER, "Name", "Craft", null, null, null);

        assertThrows(NotAnArtisanException.class, () -> handler.handle(command));

        verify(artisanProfileRepository, never()).save(any());
    }
}
