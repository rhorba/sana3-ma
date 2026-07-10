package ma.sana3.domain.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void registerCreatesUserWithLowercasedEmail() {
    User user = User.register("Someone@Example.com", "hashed-value", Role.BUYER);

    assertEquals("someone@example.com", user.email());
    assertEquals("hashed-value", user.passwordHash());
    assertEquals(Role.BUYER, user.role());
    assertNotNull(user.id());
    assertNotNull(user.createdAt());
    assertNotNull(user.updatedAt());
  }

  @Test
  void constructorRejectsBlankEmail() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new User(
                java.util.UUID.randomUUID(),
                " ",
                "hash",
                Role.BUYER,
                java.time.Instant.now(),
                java.time.Instant.now()));
  }

  @Test
  void constructorRejectsBlankPasswordHash() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new User(
                java.util.UUID.randomUUID(),
                "a@b.com",
                "",
                Role.BUYER,
                java.time.Instant.now(),
                java.time.Instant.now()));
  }
}
