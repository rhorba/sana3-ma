package ma.sana3.adapter.persistence.artisanprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCooperativeMembershipRepository
    extends JpaRepository<CooperativeMembershipJpaEntity, UUID> {

  Optional<CooperativeMembershipJpaEntity> findByUserId(UUID userId);

  List<CooperativeMembershipJpaEntity> findByArtisanProfileId(UUID artisanProfileId);

  boolean existsByUserId(UUID userId);
}
