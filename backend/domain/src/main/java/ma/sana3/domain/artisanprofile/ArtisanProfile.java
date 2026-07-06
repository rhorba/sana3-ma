package ma.sana3.domain.artisanprofile;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class ArtisanProfile {

    private final UUID id;
    private final UUID userId;
    private final String displayName;
    private final String craftType;
    private final String region;
    private final String bio;
    private final String contactPhone;
    private final Instant createdAt;
    private final Instant updatedAt;

    public ArtisanProfile(
            UUID id,
            UUID userId,
            String displayName,
            String craftType,
            String region,
            String bio,
            String contactPhone,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.displayName = requireNonBlank(displayName, "displayName");
        this.craftType = requireNonBlank(craftType, "craftType");
        this.region = region;
        this.bio = bio;
        this.contactPhone = contactPhone;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static ArtisanProfile create(
            UUID userId, String displayName, String craftType, String region, String bio, String contactPhone) {
        Instant now = Instant.now();
        return new ArtisanProfile(UUID.randomUUID(), userId, displayName, craftType, region, bio, contactPhone, now, now);
    }

    public ArtisanProfile withDetails(String displayName, String craftType, String region, String bio, String contactPhone) {
        return new ArtisanProfile(id, userId, displayName, craftType, region, bio, contactPhone, createdAt, Instant.now());
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

    public UUID userId() {
        return userId;
    }

    public String displayName() {
        return displayName;
    }

    public String craftType() {
        return craftType;
    }

    public String region() {
        return region;
    }

    public String bio() {
        return bio;
    }

    public String contactPhone() {
        return contactPhone;
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
        if (!(o instanceof ArtisanProfile other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
