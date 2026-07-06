package ma.sana3.adapter.web.artisanprofile;

import jakarta.validation.Valid;
import ma.sana3.application.artisanprofile.ArtisanProfileResult;
import ma.sana3.application.artisanprofile.GetArtisanProfileHandler;
import ma.sana3.application.artisanprofile.GetArtisanProfileQuery;
import ma.sana3.application.artisanprofile.UpdateArtisanProfileCommand;
import ma.sana3.application.artisanprofile.UpdateArtisanProfileHandler;
import ma.sana3.domain.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artisan-profiles")
class ArtisanProfileController {

    private static final String ROLE_PREFIX = "ROLE_";

    private final UpdateArtisanProfileHandler updateArtisanProfileHandler;
    private final GetArtisanProfileHandler getArtisanProfileHandler;

    ArtisanProfileController(UpdateArtisanProfileHandler updateArtisanProfileHandler, GetArtisanProfileHandler getArtisanProfileHandler) {
        this.updateArtisanProfileHandler = updateArtisanProfileHandler;
        this.getArtisanProfileHandler = getArtisanProfileHandler;
    }

    @PutMapping("/me")
    ArtisanProfileResponse upsert(
            @AuthenticationPrincipal UUID userId,
            Authentication authentication,
            @Valid @RequestBody UpsertArtisanProfileRequest request
    ) {
        ArtisanProfileResult result = updateArtisanProfileHandler.handle(new UpdateArtisanProfileCommand(
                userId,
                roleOf(authentication),
                request.displayName(),
                request.craftType(),
                request.region(),
                request.bio(),
                request.contactPhone()));
        return ArtisanProfileResponse.from(result);
    }

    @GetMapping("/me")
    ArtisanProfileResponse get(@AuthenticationPrincipal UUID userId) {
        ArtisanProfileResult result = getArtisanProfileHandler.handle(new GetArtisanProfileQuery(userId));
        return ArtisanProfileResponse.from(result);
    }

    private static Role roleOf(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> Role.valueOf(authority.substring(ROLE_PREFIX.length())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Authenticated request missing a role authority"));
    }
}
