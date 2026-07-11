package ma.sana3.domain.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  User save(User user);

  Optional<User> findByEmail(String email);

  Optional<User> findById(UUID id);

  List<User> findByIds(Collection<UUID> ids);

  boolean existsByEmail(String email);
}
