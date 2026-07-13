package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import org.springframework.stereotype.Service;

@Service
public class GetArtisanProfileHandler {

  private final ArtisanProfileRepository artisanProfileRepository;
  private final CooperativeMembershipRepository membershipRepository;

  public GetArtisanProfileHandler(
      ArtisanProfileRepository artisanProfileRepository,
      CooperativeMembershipRepository membershipRepository) {
    this.artisanProfileRepository = artisanProfileRepository;
    this.membershipRepository = membershipRepository;
  }

  public ArtisanProfileResult handle(GetArtisanProfileQuery query) {
    return membershipRepository
        .findByUserId(query.userId())
        .flatMap(membership -> artisanProfileRepository.findById(membership.artisanProfileId()))
        .map(ArtisanProfileResultMapper::toResult)
        .orElseThrow(ProfileNotFoundException::new);
  }
}
