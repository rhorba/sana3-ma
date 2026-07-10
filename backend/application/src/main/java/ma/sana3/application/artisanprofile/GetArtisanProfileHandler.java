package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class GetArtisanProfileHandler {

  private final ArtisanProfileRepository artisanProfileRepository;

  public GetArtisanProfileHandler(ArtisanProfileRepository artisanProfileRepository) {
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public ArtisanProfileResult handle(GetArtisanProfileQuery query) {
    return artisanProfileRepository
        .findByUserId(query.userId())
        .map(ArtisanProfileResultMapper::toResult)
        .orElseThrow(ProfileNotFoundException::new);
  }
}
