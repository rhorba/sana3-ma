package ma.sana3.application.artisanprofile;

import java.util.UUID;

public record RespondToInviteCommand(UUID userId, UUID inviteId) {}
