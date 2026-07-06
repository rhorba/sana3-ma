package ma.sana3.adapter.web.artisanprofile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpsertArtisanProfileRequest(
        @NotBlank @Size(max = 150) String displayName,
        @NotBlank @Size(max = 100) String craftType,
        @Size(max = 100) String region,
        String bio,
        @Size(max = 30) String contactPhone
) {
}
