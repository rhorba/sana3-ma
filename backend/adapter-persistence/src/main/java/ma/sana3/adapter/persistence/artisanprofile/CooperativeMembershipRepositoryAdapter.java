package ma.sana3.adapter.persistence.artisanprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import org.springframework.stereotype.Repository;

@Repository
class CooperativeMembershipRepositoryAdapter implements CooperativeMembershipRepository {

  private final SpringDataCooperativeMembershipRepository springDataRepository;

  CooperativeMembershipRepositoryAdapter(
      SpringDataCooperativeMembershipRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public CooperativeMembership save(CooperativeMembership membership) {
    CooperativeMembershipJpaEntity saved =
        springDataRepository.save(CooperativeMembershipEntityMapper.toEntity(membership));
    return CooperativeMembershipEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<CooperativeMembership> findByUserId(UUID userId) {
    return springDataRepository
        .findByUserId(userId)
        .map(CooperativeMembershipEntityMapper::toDomain);
  }

  @Override
  public List<CooperativeMembership> findByArtisanProfileId(UUID artisanProfileId) {
    return springDataRepository.findByArtisanProfileId(artisanProfileId).stream()
        .map(CooperativeMembershipEntityMapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsByUserId(UUID userId) {
    return springDataRepository.existsByUserId(userId);
  }

  @Override
  public void delete(CooperativeMembership membership) {
    springDataRepository.deleteById(membership.id());
  }
}
