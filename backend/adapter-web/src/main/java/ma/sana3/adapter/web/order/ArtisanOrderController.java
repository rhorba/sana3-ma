package ma.sana3.adapter.web.order;

import java.util.List;
import java.util.UUID;
import ma.sana3.application.order.ArtisanOrderItemResult;
import ma.sana3.application.order.CompleteArtisanOrderItemCommand;
import ma.sana3.application.order.CompleteArtisanOrderItemHandler;
import ma.sana3.application.order.ListArtisanOrderItemsHandler;
import ma.sana3.application.order.ListArtisanOrderItemsQuery;
import ma.sana3.domain.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artisan-profiles/me/orders")
class ArtisanOrderController {

  private static final String ROLE_PREFIX = "ROLE_";

  private final ListArtisanOrderItemsHandler listArtisanOrderItemsHandler;
  private final CompleteArtisanOrderItemHandler completeArtisanOrderItemHandler;

  ArtisanOrderController(
      ListArtisanOrderItemsHandler listArtisanOrderItemsHandler,
      CompleteArtisanOrderItemHandler completeArtisanOrderItemHandler) {
    this.listArtisanOrderItemsHandler = listArtisanOrderItemsHandler;
    this.completeArtisanOrderItemHandler = completeArtisanOrderItemHandler;
  }

  @GetMapping
  List<ArtisanOrderItemResponse> list(
      @AuthenticationPrincipal UUID userId, Authentication authentication) {
    List<ArtisanOrderItemResult> results =
        listArtisanOrderItemsHandler.handle(
            new ListArtisanOrderItemsQuery(userId, roleOf(authentication)));
    return results.stream().map(ArtisanOrderItemResponse::from).toList();
  }

  @PostMapping("/{id}/complete")
  ArtisanOrderItemResponse complete(
      @AuthenticationPrincipal UUID userId, Authentication authentication, @PathVariable UUID id) {
    ArtisanOrderItemResult result =
        completeArtisanOrderItemHandler.handle(
            new CompleteArtisanOrderItemCommand(userId, roleOf(authentication), id));
    return ArtisanOrderItemResponse.from(result);
  }

  private static Role roleOf(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> authority.startsWith(ROLE_PREFIX))
        .map(authority -> Role.valueOf(authority.substring(ROLE_PREFIX.length())))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("Authenticated request missing a role authority"));
  }
}
