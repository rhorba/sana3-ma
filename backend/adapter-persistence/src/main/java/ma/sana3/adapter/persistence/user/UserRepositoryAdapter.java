package ma.sana3.adapter.persistence.user;

import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    UserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = springDataUserRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id).map(UserEntityMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }
}
