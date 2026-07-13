package ma.sana3.application.artisanprofile;

import java.util.UUID;
import ma.sana3.domain.user.Role;

public record RemoveMemberCommand(UUID userId, Role userRole, UUID targetUserId) {}
