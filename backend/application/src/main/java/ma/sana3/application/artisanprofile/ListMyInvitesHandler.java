package ma.sana3.application.artisanprofile;

import java.util.List;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import org.springframework.stereotype.Service;

@Service
public class ListMyInvitesHandler {

  private final CooperativeInviteRepository inviteRepository;
  private final ArtisanProfileRepository artisanProfileRepository;

  public ListMyInvitesHandler(
      CooperativeInviteRepository inviteRepository,
      ArtisanProfileRepository artisanProfileRepository) {
    this.inviteRepository = inviteRepository;
    this.artisanProfileRepository = artisanProfileRepository;
  }

  public List<CooperativeInviteResult> handle(ListMyInvitesQuery query) {
    List<CooperativeInvite> invites = inviteRepository.findPendingByInvitedUserId(query.userId());
    return invites.stream()
        .map(
            invite -> {
              String artisanDisplayName =
                  artisanProfileRepository
                      .findById(invite.artisanProfileId())
                      .map(profile -> profile.displayName())
                      .orElse(null);
              return new CooperativeInviteResult(
                  invite.id(),
                  invite.artisanProfileId(),
                  artisanDisplayName,
                  invite.status(),
                  invite.createdAt());
            })
        .toList();
  }
}
