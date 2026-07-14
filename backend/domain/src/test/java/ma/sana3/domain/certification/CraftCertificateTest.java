package ma.sana3.domain.certification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CraftCertificateTest {

  @Test
  void issueBuildsACertificate() {
    UUID productId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();

    CraftCertificate certificate = CraftCertificate.issue(productId, artisanProfileId);

    assertEquals(productId, certificate.productId());
    assertEquals(artisanProfileId, certificate.artisanProfileId());
    assertNotNull(certificate.id());
    assertNotNull(certificate.issuedAt());
  }

  @Test
  void constructorRejectsNullProductId() {
    assertThrows(
        NullPointerException.class,
        () -> new CraftCertificate(UUID.randomUUID(), null, UUID.randomUUID(), Instant.now()));
  }

  @Test
  void equalityIsByIdOnly() {
    UUID id = UUID.randomUUID();
    CraftCertificate a =
        new CraftCertificate(id, UUID.randomUUID(), UUID.randomUUID(), Instant.now());
    CraftCertificate b =
        new CraftCertificate(id, UUID.randomUUID(), UUID.randomUUID(), Instant.now());

    assertEquals(a, b);
  }
}
