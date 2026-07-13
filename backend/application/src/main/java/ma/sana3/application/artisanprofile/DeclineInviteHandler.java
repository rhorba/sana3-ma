package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import org.springframework.stereotype.Service;

@Service
public class DeclineInviteHandler {

  private final CooperativeInviteRepository inviteRepository;

  public DeclineInviteHandler(CooperativeInviteRepository inviteRepository) {
    this.inviteRepository = inviteRepository;
  }

  public CooperativeInviteResult handle(RespondToInviteCommand command) {
    CooperativeInvite invite =
        inviteRepository
            .findById(command.inviteId())
            .filter(existing -> existing.invitedUserId().equals(command.userId()))
            .orElseThrow(InviteNotFoundException::new);

    CooperativeInvite declined = inviteRepository.save(invite.decline());

    return new CooperativeInviteResult(
        declined.id(), declined.artisanProfileId(), null, declined.status(), declined.createdAt());
  }
}
