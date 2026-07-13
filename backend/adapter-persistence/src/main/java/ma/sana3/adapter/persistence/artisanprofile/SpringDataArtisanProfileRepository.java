package ma.sana3.adapter.persistence.artisanprofile;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataArtisanProfileRepository extends JpaRepository<ArtisanProfileJpaEntity, UUID> {}
