package ma.sana3.domain.user;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class User {

  private final UUID id;
  private final String email;
  private final String passwordHash;
  private final Role role;
  private final Instant createdAt;
  private final Instant updatedAt;

  public User(
      UUID id, String email, String passwordHash, Role role, Instant createdAt, Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.email = requireNonBlank(email, "email");
    this.passwordHash = requireNonBlank(passwordHash, "passwordHash");
    this.role = Objects.requireNonNull(role, "role");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  public static User register(String email, String passwordHash, Role role) {
    Instant now = Instant.now();
    return new User(UUID.randomUUID(), email.toLowerCase(), passwordHash, role, now, now);
  }

  private static String requireNonBlank(String value, String field) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
    return value;
  }

  public UUID id() {
    return id;
  }

  public String email() {
    return email;
  }

  public String passwordHash() {
    return passwordHash;
  }

  public Role role() {
    return role;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User other)) return false;
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
