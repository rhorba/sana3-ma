package ma.sana3.adapter.persistence.artisanprofile;

import java.util.List;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCooperativeInviteRepository
    extends JpaRepository<CooperativeInviteJpaEntity, UUID> {

  List<CooperativeInviteJpaEntity> findByInvitedUserIdAndStatus(
      UUID invitedUserId, InviteStatus status);

  boolean existsByInvitedUserIdAndStatus(UUID invitedUserId, InviteStatus status);
}
