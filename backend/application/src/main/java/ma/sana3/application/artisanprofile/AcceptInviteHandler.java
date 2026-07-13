package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import org.springframework.stereotype.Service;

@Service
public class AcceptInviteHandler {

  private final CooperativeInviteRepository inviteRepository;
  private final CooperativeMembershipRepository membershipRepository;

  public AcceptInviteHandler(
      CooperativeInviteRepository inviteRepository,
      CooperativeMembershipRepository membershipRepository) {
    this.inviteRepository = inviteRepository;
    this.membershipRepository = membershipRepository;
  }

  public CooperativeInviteResult handle(RespondToInviteCommand command) {
    CooperativeInvite invite =
        inviteRepository
            .findById(command.inviteId())
            .filter(existing -> existing.invitedUserId().equals(command.userId()))
            .orElseThrow(InviteNotFoundException::new);

    if (membershipRepository.existsByUserId(command.userId())) {
      throw new InviteeAlreadyMemberException();
    }

    CooperativeInvite accepted = inviteRepository.save(invite.accept());
    membershipRepository.save(
        CooperativeMembership.create(
            command.userId(), accepted.artisanProfileId(), MembershipRole.MEMBER));

    return new CooperativeInviteResult(
        accepted.id(), accepted.artisanProfileId(), null, accepted.status(), accepted.createdAt());
  }
}
