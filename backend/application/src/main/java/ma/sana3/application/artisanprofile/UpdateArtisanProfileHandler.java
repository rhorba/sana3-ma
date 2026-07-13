package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class UpdateArtisanProfileHandler {

  private final ArtisanProfileRepository artisanProfileRepository;
  private final CooperativeMembershipRepository membershipRepository;

  public UpdateArtisanProfileHandler(
      ArtisanProfileRepository artisanProfileRepository,
      CooperativeMembershipRepository membershipRepository) {
    this.artisanProfileRepository = artisanProfileRepository;
    this.membershipRepository = membershipRepository;
  }

  public ArtisanProfileResult handle(UpdateArtisanProfileCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }

    var membership = membershipRepository.findByUserId(command.userId());

    if (membership.isPresent()) {
      ArtisanProfile existing =
          artisanProfileRepository
              .findById(membership.get().artisanProfileId())
              .orElseThrow(ProfileNotFoundException::new);
      ArtisanProfile saved =
          artisanProfileRepository.save(
              existing.withDetails(
                  command.displayName(),
                  command.craftType(),
                  command.region(),
                  command.bio(),
                  command.contactPhone()));
      return ArtisanProfileResultMapper.toResult(saved);
    }

    ArtisanProfile created =
        artisanProfileRepository.save(
            ArtisanProfile.create(
                command.displayName(),
                command.craftType(),
                command.region(),
                command.bio(),
                command.contactPhone()));
    membershipRepository.save(
        CooperativeMembership.create(command.userId(), created.id(), MembershipRole.OWNER));
    return ArtisanProfileResultMapper.toResult(created);
  }
}
