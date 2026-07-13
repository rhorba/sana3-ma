package ma.sana3.application.artisanprofile;

import java.time.Instant;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.InviteStatus;

public record CooperativeInviteResult(
    UUID id,
    UUID artisanProfileId,
    String artisanDisplayName,
    InviteStatus status,
    Instant createdAt) {}
