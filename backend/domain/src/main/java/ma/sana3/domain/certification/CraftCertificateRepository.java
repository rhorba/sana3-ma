package ma.sana3.domain.certification;

import java.util.Optional;
import java.util.UUID;

public interface CraftCertificateRepository {

  CraftCertificate save(CraftCertificate certificate);

  Optional<CraftCertificate> findById(UUID id);

  Optional<CraftCertificate> findByProductId(UUID productId);
}
