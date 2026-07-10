package ma.sana3.application.catalog;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record UploadProductImageCommand(
    UUID userId, Role userRole, UUID productId, byte[] content, String contentType) {}
