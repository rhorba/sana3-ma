package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class InviteMemberHandler {

  private final CooperativeMembershipRepository membershipRepository;
  private final CooperativeInviteRepository inviteRepository;
  private final UserRepository userRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public InviteMemberHandler(
      CooperativeMembershipRepository membershipRepository,
      CooperativeInviteRepository inviteRepository,
      UserRepository userRepository,
      ArtisanProfileRepository artisanProfileRepository) {
    this.membershipRepository = membershipRepository;
    this.inviteRepository = inviteRepository;
    this.userRepository = userRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public CooperativeInviteResult handle(InviteMemberCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }
    var membership =
        membershipRepository
            .findByUserId(command.userId())
            .orElseThrow(ProfileNotFoundException::new);
    if (!membership.isOwner()) {
      throw new NotCooperativeOwnerException();
    }

    User invitee =
        userRepository
            .findByEmail(command.inviteeEmail().toLowerCase())
            .filter(user -> user.role() == Role.ARTISAN)
            .orElseThrow(InviteeNotEligibleException::new);

    if (membershipRepository.existsByUserId(invitee.id())) {
      throw new InviteeAlreadyMemberException();
    }
    if (inviteRepository.existsPendingByInvitedUserId(invitee.id())) {
      throw new InviteeAlreadyInvitedException();
    }

    CooperativeInvite saved =
        inviteRepository.save(
            CooperativeInvite.create(membership.artisanProfileId(), invitee.id()));
    String artisanDisplayName =
        artisanProfileRepository
            .findById(membership.artisanProfileId())
            .map(profile -> profile.displayName())
            .orElse(null);
    return new CooperativeInviteResult(
        saved.id(),
        saved.artisanProfileId(),
        artisanDisplayName,
        saved.status(),
        saved.createdAt());
  }
}
