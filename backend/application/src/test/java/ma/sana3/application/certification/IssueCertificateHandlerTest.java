package ma.sana3.application.certification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.application.artisanprofile.NotAnArtisanException;
import ma.sana3.application.artisanprofile.ProfileNotFoundException;
import ma.sana3.application.catalog.ProductNotFoundException;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import ma.sana3.domain.catalog.Product;
import ma.sana3.domain.catalog.ProductRepository;
import ma.sana3.domain.certification.CraftCertificate;
import ma.sana3.domain.certification.CraftCertificateRepository;
import ma.sana3.domain.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IssueCertificateHandlerTest {

  @Mock private CraftCertificateRepository certificateRepository;
  @Mock private ProductRepository productRepository;
  @Mock private CooperativeMembershipRepository membershipRepository;

  private IssueCertificateHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new IssueCertificateHandler(certificateRepository, productRepository, membershipRepository);
  }

  @Test
  void issuesANewCertificateWhenNoneExists() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product product =
        Product.create(
            artisanProfileId, "Rug", null, new BigDecimal("100.00"), "MAD", "Weaving", null);
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.OWNER)));
    when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
    when(certificateRepository.findByProductId(product.id())).thenReturn(Optional.empty());
    when(certificateRepository.save(any(CraftCertificate.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CertificateResult result =
        handler.handle(new IssueCertificateCommand(userId, Role.ARTISAN, product.id()));

    assertEquals(product.id(), result.productId());
    verify(certificateRepository).save(any(CraftCertificate.class));
  }

  @Test
  void issuingIsIdempotentAndReturnsTheExistingCertificate() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product product =
        Product.create(
            artisanProfileId, "Rug", null, new BigDecimal("100.00"), "MAD", "Weaving", null);
    CraftCertificate existing = CraftCertificate.issue(product.id(), artisanProfileId);
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.MEMBER)));
    when(productRepository.findById(product.id())).thenReturn(Optional.of(product));
    when(certificateRepository.findByProductId(product.id())).thenReturn(Optional.of(existing));

    CertificateResult result =
        handler.handle(new IssueCertificateCommand(userId, Role.ARTISAN, product.id()));

    assertEquals(existing.id(), result.id());
    verify(certificateRepository, never()).save(any());
  }

  @Test
  void rejectsIssuingForSomeoneElsesProduct() {
    UUID userId = UUID.randomUUID();
    UUID artisanProfileId = UUID.randomUUID();
    Product othersProduct =
        Product.create(
            UUID.randomUUID(), "Rug", null, new BigDecimal("100.00"), "MAD", "Weaving", null);
    when(membershipRepository.findByUserId(userId))
        .thenReturn(
            Optional.of(
                CooperativeMembership.create(userId, artisanProfileId, MembershipRole.OWNER)));
    when(productRepository.findById(othersProduct.id())).thenReturn(Optional.of(othersProduct));

    assertThrows(
        ProductNotFoundException.class,
        () ->
            handler.handle(new IssueCertificateCommand(userId, Role.ARTISAN, othersProduct.id())));
  }

  @Test
  void rejectsNonArtisanRole() {
    UUID userId = UUID.randomUUID();

    assertThrows(
        NotAnArtisanException.class,
        () -> handler.handle(new IssueCertificateCommand(userId, Role.BUYER, UUID.randomUUID())));
  }

  @Test
  void rejectsUserWithoutMembership() {
    UUID userId = UUID.randomUUID();
    when(membershipRepository.findByUserId(userId)).thenReturn(Optional.empty());

    assertThrows(
        ProfileNotFoundException.class,
        () -> handler.handle(new IssueCertificateCommand(userId, Role.ARTISAN, UUID.randomUUID())));
  }
}
