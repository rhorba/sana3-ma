package ma.sana3.application.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.user.Role;
import org.springframework.stereotype.Service;

@Service
public class UpdateArtisanProfileHandler {

  private final ArtisanProfileRepository artisanProfileRepository;

  public UpdateArtisanProfileHandler(ArtisanProfileRepository artisanProfileRepository) {
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public ArtisanProfileResult handle(UpdateArtisanProfileCommand command) {
    if (command.userRole() != Role.ARTISAN) {
      throw new NotAnArtisanException();
    }

    ArtisanProfile profile =
        artisanProfileRepository
            .findByUserId(command.userId())
            .map(
                existing ->
                    existing.withDetails(
                        command.displayName(),
                        command.craftType(),
                        command.region(),
                        command.bio(),
                        command.contactPhone()))
            .orElseGet(
                () ->
                    ArtisanProfile.create(
                        command.userId(),
                        command.displayName(),
                        command.craftType(),
                        command.region(),
                        command.bio(),
                        command.contactPhone()));

    ArtisanProfile saved = artisanProfileRepository.save(profile);
    return ArtisanProfileResultMapper.toResult(saved);
  }
}
