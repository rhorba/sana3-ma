package ma.sana3.adapter.persistence.artisanprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import ma.sana3.domain.artisanprofile.InviteStatus;
import org.springframework.stereotype.Repository;

@Repository
class CooperativeInviteRepositoryAdapter implements CooperativeInviteRepository {

  private final SpringDataCooperativeInviteRepository springDataRepository;

  CooperativeInviteRepositoryAdapter(SpringDataCooperativeInviteRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public CooperativeInvite save(CooperativeInvite invite) {
    CooperativeInviteJpaEntity saved =
        springDataRepository.save(CooperativeInviteEntityMapper.toEntity(invite));
    return CooperativeInviteEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<CooperativeInvite> findById(UUID id) {
    return springDataRepository.findById(id).map(CooperativeInviteEntityMapper::toDomain);
  }

  @Override
  public List<CooperativeInvite> findPendingByInvitedUserId(UUID invitedUserId) {
    return springDataRepository
        .findByInvitedUserIdAndStatus(invitedUserId, InviteStatus.PENDING)
        .stream()
        .map(CooperativeInviteEntityMapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsPendingByInvitedUserId(UUID invitedUserId) {
    return springDataRepository.existsByInvitedUserIdAndStatus(invitedUserId, InviteStatus.PENDING);
  }
}
