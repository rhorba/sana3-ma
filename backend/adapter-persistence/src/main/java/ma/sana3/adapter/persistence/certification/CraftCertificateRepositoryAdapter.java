package ma.sana3.adapter.persistence.certification;

import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.certification.CraftCertificate;
import ma.sana3.domain.certification.CraftCertificateRepository;
import org.springframework.stereotype.Repository;

@Repository
class CraftCertificateRepositoryAdapter implements CraftCertificateRepository {

  private final SpringDataCraftCertificateRepository springDataRepository;

  CraftCertificateRepositoryAdapter(SpringDataCraftCertificateRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public CraftCertificate save(CraftCertificate certificate) {
    CraftCertificateJpaEntity saved =
        springDataRepository.save(CraftCertificateEntityMapper.toEntity(certificate));
    return CraftCertificateEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<CraftCertificate> findById(UUID id) {
    return springDataRepository.findById(id).map(CraftCertificateEntityMapper::toDomain);
  }

  @Override
  public Optional<CraftCertificate> findByProductId(UUID productId) {
    return springDataRepository
        .findByProductId(productId)
        .map(CraftCertificateEntityMapper::toDomain);
  }
}
