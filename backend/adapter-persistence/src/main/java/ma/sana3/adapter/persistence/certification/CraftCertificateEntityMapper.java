package ma.sana3.adapter.persistence.certification;

import ma.sana3.domain.certification.CraftCertificate;

final class CraftCertificateEntityMapper {

  private CraftCertificateEntityMapper() {}

  static CraftCertificate toDomain(CraftCertificateJpaEntity entity) {
    return new CraftCertificate(
        entity.getId(), entity.getProductId(), entity.getArtisanProfileId(), entity.getIssuedAt());
  }

  static CraftCertificateJpaEntity toEntity(CraftCertificate certificate) {
    return new CraftCertificateJpaEntity(
        certificate.id(),
        certificate.productId(),
        certificate.artisanProfileId(),
        certificate.issuedAt());
  }
}
