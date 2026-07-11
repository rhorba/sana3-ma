package ma.sana3.adapter.persistence.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepositoryAdapter.class)
@Testcontainers
class UserRepositoryAdapterTest {

  @Container @ServiceConnection
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(
          DockerImageName.parse("postgis/postgis:16-3.4-alpine")
              .asCompatibleSubstituteFor("postgres"));

  @Autowired private UserRepositoryAdapter repository;

  @Test
  void savesAndFindsByEmail() {
    User user = User.register("test@example.com", "hash", Role.BUYER);
    repository.save(user);

    Optional<User> found = repository.findByEmail("test@example.com");

    assertTrue(found.isPresent());
    assertEquals(user.id(), found.get().id());
    assertEquals(Role.BUYER, found.get().role());
  }

  @Test
  void existsByEmailReflectsSavedUsers() {
    assertFalse(repository.existsByEmail("nobody@example.com"));

    repository.save(User.register("somebody@example.com", "hash", Role.ARTISAN));

    assertTrue(repository.existsByEmail("somebody@example.com"));
  }

  @Test
  void findByIdReturnsEmptyForUnknownId() {
    assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
  }

  @Test
  void findsByIds() {
    User first = repository.save(User.register("first@example.com", "hash", Role.BUYER));
    User second = repository.save(User.register("second@example.com", "hash", Role.ARTISAN));

    List<User> found = repository.findByIds(List.of(first.id(), second.id()));

    assertEquals(2, found.size());
  }
}
