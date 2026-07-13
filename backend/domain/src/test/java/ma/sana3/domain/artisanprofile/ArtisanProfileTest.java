package ma.sana3.domain.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArtisanProfileTest {

  @Test
  void createBuildsProfile() {
    ArtisanProfile profile =
        ArtisanProfile.create("Fatima Zahra", "Pottery", "Fes", "Bio", "+212600000000");

    assertEquals("Fatima Zahra", profile.displayName());
    assertEquals("Pottery", profile.craftType());
    assertNotNull(profile.id());
    assertNotNull(profile.createdAt());
    assertNotNull(profile.updatedAt());
  }

  @Test
  void withDetailsKeepsIdentityAndBumpsUpdatedAt() {
    ArtisanProfile profile = ArtisanProfile.create("Name", "Craft", null, null, null);

    ArtisanProfile updated =
        profile.withDetails("New Name", "New Craft", "Rabat", "New bio", "+212611111111");

    assertEquals(profile.id(), updated.id());
    assertEquals(profile.createdAt(), updated.createdAt());
    assertEquals("New Name", updated.displayName());
    assertEquals("New Craft", updated.craftType());
    assertEquals("Rabat", updated.region());
  }

  @Test
  void constructorRejectsBlankDisplayName() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ArtisanProfile(
                UUID.randomUUID(), " ", "Craft", null, null, null, Instant.now(), Instant.now()));
  }

  @Test
  void constructorRejectsBlankCraftType() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ArtisanProfile(
                UUID.randomUUID(), "Name", "", null, null, null, Instant.now(), Instant.now()));
  }
}
