package ma.sana3.adapter.persistence.artisanprofile;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataArtisanProfileRepository extends JpaRepository<ArtisanProfileJpaEntity, UUID> {

  Optional<ArtisanProfileJpaEntity> findByUserId(UUID userId);
}
