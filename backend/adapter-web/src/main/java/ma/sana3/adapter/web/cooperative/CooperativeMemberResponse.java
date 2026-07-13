package ma.sana3.adapter.web.cooperative;

import java.time.Instant;
import java.util.UUID;
import ma.sana3.application.artisanprofile.CooperativeMemberResult;
import ma.sana3.domain.artisanprofile.MembershipRole;

public record CooperativeMemberResponse(
    UUID userId, String email, MembershipRole role, Instant joinedAt) {
  static CooperativeMemberResponse from(CooperativeMemberResult result) {
    return new CooperativeMemberResponse(
        result.userId(), result.email(), result.role(), result.joinedAt());
  }
}
