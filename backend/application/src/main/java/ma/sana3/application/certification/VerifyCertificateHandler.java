package ma.sana3.application.certification;

import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.certification.CraftCertificate;
import ma.sana3.domain.certification.CraftCertificateRepository;
import org.springframework.stereotype.Service;

@Service
public class VerifyCertificateHandler {

  private final CraftCertificateRepository certificateRepository;
  private final ProductRepository productRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public VerifyCertificateHandler(
      CraftCertificateRepository certificateRepository,
      ProductRepository productRepository,
      ArtisanProfileRepository artisanProfileRepository) {
    this.certificateRepository = certificateRepository;
    this.productRepository = productRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public CertificateVerificationResult handle(VerifyCertificateQuery query) {
    UUID id = parseCode(query.code());
    CraftCertificate certificate =
        certificateRepository.findById(id).orElseThrow(CertificateNotFoundException::new);
    // The product/profile FKs are ON DELETE CASCADE, so a certificate never outlives either —
    // a missing lookup here would mean a real data-integrity bug, not a normal "not found".
    Product product =
        productRepository
            .findById(certificate.productId())
            .orElseThrow(
                () -> new IllegalStateException("Certificate references a missing product"));
    ArtisanProfile artisan =
        artisanProfileRepository
            .findById(certificate.artisanProfileId())
            .orElseThrow(
                () ->
                    new IllegalStateException("Certificate references a missing artisan profile"));

    return new CertificateVerificationResult(
        artisan.displayName(), product.name(), product.craftType(), certificate.issuedAt());
  }

  private static UUID parseCode(String code) {
    try {
      return UUID.fromString(code);
    } catch (IllegalArgumentException e) {
      throw new CertificateNotFoundException();
    }
  }
}
