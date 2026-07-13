package ma.sana3.domain.artisanprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CooperativeInviteRepository {

  CooperativeInvite save(CooperativeInvite invite);

  Optional<CooperativeInvite> findById(UUID id);

  List<CooperativeInvite> findPendingByInvitedUserId(UUID invitedUserId);

  boolean existsPendingByInvitedUserId(UUID invitedUserId);
}
