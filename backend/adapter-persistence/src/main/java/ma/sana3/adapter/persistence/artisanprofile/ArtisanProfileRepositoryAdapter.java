package ma.sana3.adapter.persistence.artisanprofile;

import ma.sana3.domain.artisanprofile.ArtisanProfile;
import ma.sana3.domain.artisanprofile.ArtisanProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class ArtisanProfileRepositoryAdapter implements ArtisanProfileRepository {

    private final SpringDataArtisanProfileRepository springDataArtisanProfileRepository;

    ArtisanProfileRepositoryAdapter(SpringDataArtisanProfileRepository springDataArtisanProfileRepository) {
        this.springDataArtisanProfileRepository = springDataArtisanProfileRepository;
    }

    @Override
    public ArtisanProfile save(ArtisanProfile profile) {
        ArtisanProfileJpaEntity saved = springDataArtisanProfileRepository.save(ArtisanProfileEntityMapper.toEntity(profile));
        return ArtisanProfileEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<ArtisanProfile> findByUserId(UUID userId) {
        return springDataArtisanProfileRepository.findByUserId(userId).map(ArtisanProfileEntityMapper::toDomain);
    }
}
