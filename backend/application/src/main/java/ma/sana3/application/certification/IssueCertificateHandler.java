package ma.sana3.application.certification;

import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.certification.CraftCertificate;
import ma.sana3.domain.certification.CraftCertificateRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class IssueCertificateHandler {

  private final CraftCertificateRepository certificateRepository;
  private final ProductRepository productRepository;
  private final CooperativeMembershipRepository membershipRepository;

  public IssueCertificateHandler(
      CraftCertificateRepository certificateRepository,
      ProductRepository productRepository,
      CooperativeMembershipRepository membershipRepository) {
    this.certificateRepository = certificateRepository;
    this.productRepository = productRepository;
    this.membershipRepository = membershipRepository;
  }

  public CertificateResult handle(IssueCertificateCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    var artisanProfileId =
        membershipRepository
            .findByUserId(command.userId())
            .map(CooperativeMembership::artisanProfileId)
            .orElseThrow(ProfileNotFoundException::new);

    Product product =
        productRepository
            .findById(command.productId())
            .filter(p -> p.artisanProfileId().equals(artisanProfileId))
            .orElseThrow(ProductNotFoundException::new);

    CraftCertificate certificate =
        certificateRepository
            .findByProductId(product.id())
            .orElseGet(
                () ->
                    certificateRepository.save(
                        CraftCertificate.issue(product.id(), artisanProfileId)));

    return new CertificateResult(certificate.id(), certificate.productId(), certificate.issuedAt());
  }
}
