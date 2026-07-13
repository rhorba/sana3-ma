package ma.sana3.application.artisanprofile;

import java.time.Instant;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.MembershipRole;

public record CooperativeMemberResult(
    UUID userId, String email, MembershipRole role, Instant joinedAt) {}
