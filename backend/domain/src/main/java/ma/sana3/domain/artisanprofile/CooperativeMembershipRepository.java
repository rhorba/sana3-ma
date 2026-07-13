package ma.sana3.domain.artisanprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CooperativeMembershipRepository {

  CooperativeMembership save(CooperativeMembership membership);

  Optional<CooperativeMembership> findByUserId(UUID userId);

  List<CooperativeMembership> findByArtisanProfileId(UUID artisanProfileId);

  boolean existsByUserId(UUID userId);

  void delete(CooperativeMembership membership);
}
