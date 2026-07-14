package ma.sana3.application.certification;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record IssueCertificateCommand(UUID userId, Role userRole, UUID productId) {}
