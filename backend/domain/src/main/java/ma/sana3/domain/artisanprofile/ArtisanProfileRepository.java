package ma.sana3.domain.artisanprofile;

import java.util.Optional;
import java.util.UUID;

public interface ArtisanProfileRepository {

  ArtisanProfile save(ArtisanProfile profile);

  Optional<ArtisanProfile> findByUserId(UUID userId);
}
