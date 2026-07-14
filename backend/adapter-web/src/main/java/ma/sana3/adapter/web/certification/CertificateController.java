package ma.sana3.adapter.web.certification;

import java.util.UUID;
import ma.sana3.application.certification.CertificateResult;
import ma.sana3.application.certification.IssueCertificateCommand;
import ma.sana3.application.certification.IssueCertificateHandler;
import ma.sana3.domain.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artisan-profiles/me/products/{productId}/certificate")
class CertificateController {

  private static final String ROLE_PREFIX = "ROLE_";

  private final IssueCertificateHandler issueCertificateHandler;

  CertificateController(IssueCertificateHandler issueCertificateHandler) {
    this.issueCertificateHandler = issueCertificateHandler;
  }

  @PostMapping
  CertificateResponse issue(
      @AuthenticationPrincipal UUID userId,
      Authentication authentication,
      @PathVariable UUID productId) {
    CertificateResult result =
        issueCertificateHandler.handle(
            new IssueCertificateCommand(userId, roleOf(authentication), productId));
    return CertificateResponse.from(result);
  }

  private static Role roleOf(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.startsWith(ROLE_PREFIX))
        .map(authority -> Role.valueOf(authority.substring(ROLE_PREFIX.length())))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Authenticated request missing a role authority"));
  }
}
