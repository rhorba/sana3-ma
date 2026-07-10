package ma.sana3.adapter.persistence.catalog;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {

  List<ProductJpaEntity> findByArtisanProfileId(UUID artisanProfileId);
}
