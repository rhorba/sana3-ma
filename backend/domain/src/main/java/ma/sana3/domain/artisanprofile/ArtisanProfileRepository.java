package ma.sana3.domain.artisanprofile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtisanProfileRepository {

  ArtisanProfile save(ArtisanProfile profile);

  Optional<ArtisanProfile> findByUserId(UUID userId);

  Optional<ArtisanProfile> findById(UUID id);

  List<ArtisanProfile> findByIds(Collection<UUID> ids);
}
