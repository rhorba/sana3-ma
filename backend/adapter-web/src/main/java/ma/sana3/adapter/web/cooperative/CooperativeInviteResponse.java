package ma.sana3.adapter.web.cooperative;

import java.time.Instant;
import java.util.UUID;
import ma.sana3.application.artisanprofile.CooperativeInviteResult;
import ma.sana3.domain.artisanprofile.InviteStatus;

public record CooperativeInviteResponse(
    UUID id,
    UUID artisanProfileId,
    String artisanDisplayName,
    InviteStatus status,
    Instant createdAt) {
  static CooperativeInviteResponse from(CooperativeInviteResult result) {
    return new CooperativeInviteResponse(
        result.id(),
        result.artisanProfileId(),
        result.artisanDisplayName(),
        result.status(),
        result.createdAt());
  }
}
