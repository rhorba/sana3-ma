package ma.sana3.application.certification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.certification.CraftCertificate;
import ma.sana3.domain.certification.CraftCertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VerifyCertificateHandlerTest {

  @Mock private CraftCertificateRepository certificateRepository;
  @Mock private ProductRepository productRepository;
  @Mock private ArtisanProfileRepository artisanProfileRepository;

  private VerifyCertificateHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new VerifyCertificateHandler(
            certificateRepository, productRepository, artisanProfileRepository);
  }

  @Test
  void returnsVerificationDetailsForAValidCode() {
    UUID artisanProfileId = UUID.randomUUID();
    Product product =
        Product.create(
            artisanProfileId, "Rug", null, new BigDecimal("100.00"), "MAD", "Weaving", null);
    CraftCertificate certificate = CraftCertificate.issue(product.id(), artisanProfileId);
    ArtisanProfile artisan = ArtisanProfile.create("Atlas Coop", "Weaving", "Fes", null, null);
    when(certificateRepository.findById(certificate.id())).thenReturn(Optional.of(certificate));
    when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
    when(artisanProfileRepository.findById(artisanProfileId)).thenReturn(Optional.of(artisan));

    CertificateVerificationResult result =
        handler.handle(new VerifyCertificateQuery(certificate.id().toString()));

    assertEquals("Atlas Coop", result.artisanDisplayName());
    assertEquals("Rug", result.productName());
    assertEquals("Weaving", result.craftType());
  }

  @Test
  void rejectsAnUnknownCode() {
    UUID unknownId = UUID.randomUUID();
    when(certificateRepository.findById(unknownId)).thenReturn(Optional.empty());

    assertThrows(
        CertificateNotFoundException.class,
        () -> handler.handle(new VerifyCertificateQuery(unknownId.toString())));
  }

  @Test
  void rejectsAMalformedCodeAsNotFoundRatherThanAServerError() {
    assertThrows(
        CertificateNotFoundException.class,
        () -> handler.handle(new VerifyCertificateQuery("not-a-uuid")));
  }
}
