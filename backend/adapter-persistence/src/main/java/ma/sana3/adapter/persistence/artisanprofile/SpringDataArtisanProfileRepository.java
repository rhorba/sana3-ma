package ma.sana3.adapter.persistence.artisanprofile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface SpringDataArtisanProfileRepository extends JpaRepository<ArtisanProfileJpaEntity, UUID> {

    Optional<ArtisanProfileJpaEntity> findByUserId(UUID userId);
}
