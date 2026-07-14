package ma.sana3.adapter.web.certification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import ma.sana3.application.certification.CertificateNotFoundException;
import ma.sana3.application.certification.CertificateVerificationResult;
import ma.sana3.application.certification.VerifyCertificateHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// SecurityConfig is package-private in ma.sana3.adapter.web.security, so this slice can't import
// it to exercise the real permitAll rule -- filters are disabled here to test controller/handler
// wiring in isolation. The actual "GET /api/v1/certificates/verify is public" rule is verified by
// a live smoke test against the running app instead.
@WebMvcTest(controllers = PublicCertificateController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicCertificateControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private VerifyCertificateHandler verifyCertificateHandler;

  @Test
  void verifyReturnsCertificateDetailsForAValidCodeWithNoAuthentication() throws Exception {
    when(verifyCertificateHandler.handle(any()))
        .thenReturn(
            new CertificateVerificationResult("Atlas Coop", "Rug", "Weaving", Instant.now()));

    mockMvc
        .perform(get("/api/v1/certificates/verify/some-code"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.artisanDisplayName").value("Atlas Coop"))
        .andExpect(jsonPath("$.productName").value("Rug"));
  }

  @Test
  void verifyReturnsNotFoundForAnUnknownCode() throws Exception {
    when(verifyCertificateHandler.handle(any())).thenThrow(new CertificateNotFoundException());

    mockMvc
        .perform(get("/api/v1/certificates/verify/unknown-code"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("CERTIFICATE_NOT_FOUND"));
  }
}
