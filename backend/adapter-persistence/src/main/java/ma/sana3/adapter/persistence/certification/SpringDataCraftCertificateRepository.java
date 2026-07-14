package ma.sana3.adapter.persistence.certification;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCraftCertificateRepository
    extends JpaRepository<CraftCertificateJpaEntity, UUID> {

  Optional<CraftCertificateJpaEntity> findByProductId(UUID productId);
}
