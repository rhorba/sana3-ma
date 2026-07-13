package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class RemoveMemberHandler {

  private final CooperativeMembershipRepository membershipRepository;

  public RemoveMemberHandler(CooperativeMembershipRepository membershipRepository) {
    this.membershipRepository = membershipRepository;
  }

  public void handle(RemoveMemberCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    var requesterMembership =
        membershipRepository
            .findByUserId(command.userId())
            .orElseThrow(ProfileNotFoundException::new);

    CooperativeMembership target =
        membershipRepository
            .findByUserId(command.targetUserId())
            .filter(
                membership ->
                    membership.artisanProfileId().equals(requesterMembership.artisanProfileId()))
            .orElseThrow(MemberNotFoundException::new);

    if (target.isOwner()) {
      throw new CannotRemoveOwnerException();
    }
    boolean isSelfRemoval = command.userId().equals(command.targetUserId());
    if (!isSelfRemoval && !requesterMembership.isOwner()) {
      throw new NotCooperativeOwnerException();
    }

    membershipRepository.delete(target);
  }
}
