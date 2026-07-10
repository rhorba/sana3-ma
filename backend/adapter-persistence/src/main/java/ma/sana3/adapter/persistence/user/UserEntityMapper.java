package ma.sana3.adapter.persistence.user;

import ma.sana3.domain.user.User;

final class UserEntityMapper {

  private UserEntityMapper() {}

  static User toDomain(UserJpaEntity entity) {
    return new User(
        entity.getId(),
        entity.getEmail(),
        entity.getPasswordHash(),
        entity.getRole(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  static UserJpaEntity toEntity(User user) {
    return new UserJpaEntity(
        user.id(),
        user.email(),
        user.passwordHash(),
        user.role(),
        user.createdAt(),
        user.updatedAt());
  }
}
