package ma.sana3.adapter.persistence.artisanprofile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import org.springframework.stereotype.Repository;

@Repository
class ArtisanProfileRepositoryAdapter implements ArtisanProfileRepository {

  private final SpringDataArtisanProfileRepository springDataArtisanProfileRepository;

  ArtisanProfileRepositoryAdapter(
      SpringDataArtisanProfileRepository springDataArtisanProfileRepository) {
    this.springDataArtisanProfileRepository = springDataArtisanProfileRepository;
  }

  @Override
  public ArtisanProfile save(ArtisanProfile profile) {
    ArtisanProfileJpaEntity saved =
        springDataArtisanProfileRepository.save(ArtisanProfileEntityMapper.toEntity(profile));
    return ArtisanProfileEntityMapper.toDomain(saved);
  }

  @Override
  public Optional<ArtisanProfile> findByUserId(UUID userId) {
    return springDataArtisanProfileRepository
        .findByUserId(userId)
        .map(ArtisanProfileEntityMapper::toDomain);
  }

  @Override
  public Optional<ArtisanProfile> findById(UUID id) {
    return springDataArtisanProfileRepository
        .findById(id)
        .map(ArtisanProfileEntityMapper::toDomain);
  }

  @Override
  public List<ArtisanProfile> findByIds(Collection<UUID> ids) {
    return springDataArtisanProfileRepository.findAllById(ids).stream()
        .map(ArtisanProfileEntityMapper::toDomain)
        .toList();
  }
}
