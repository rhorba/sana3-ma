package ma.sana3.adapter.web.certification;

import ma.sana3.application.certification.VerifyCertificateHandler;
import ma.sana3.application.certification.VerifyCertificateQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/certificates")
class PublicCertificateController {

  private final VerifyCertificateHandler verifyCertificateHandler;

  PublicCertificateController(VerifyCertificateHandler verifyCertificateHandler) {
    this.verifyCertificateHandler = verifyCertificateHandler;
  }

  @GetMapping("/verify/{code}")
  CertificateVerificationResponse verify(@PathVariable String code) {
    return CertificateVerificationResponse.from(
        verifyCertificateHandler.handle(new VerifyCertificateQuery(code)));
  }
}
