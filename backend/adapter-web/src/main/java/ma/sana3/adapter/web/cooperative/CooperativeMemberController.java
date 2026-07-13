package ma.sana3.adapter.web.cooperative;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.artisanprofile.CooperativeInviteResult;
import ma.sana3.application.artisanprofile.CooperativeMemberResult;
import ma.sana3.application.artisanprofile.InviteMemberCommand;
import ma.sana3.application.artisanprofile.InviteMemberHandler;
import ma.sana3.application.artisanprofile.ListMembersHandler;
import ma.sana3.application.artisanprofile.ListMembersQuery;
import ma.sana3.application.artisanprofile.RemoveMemberCommand;
import ma.sana3.application.artisanprofile.RemoveMemberHandler;
import ma.sana3.domain.user.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artisan-profiles/me/members")
class CooperativeMemberController {

  private static final String ROLE_PREFIX = "ROLE_";

  private final ListMembersHandler listMembersHandler;
  private final InviteMemberHandler inviteMemberHandler;
  private final RemoveMemberHandler removeMemberHandler;

  CooperativeMemberController(
      ListMembersHandler listMembersHandler,
      InviteMemberHandler inviteMemberHandler,
      RemoveMemberHandler removeMemberHandler) {
    this.listMembersHandler = listMembersHandler;
    this.inviteMemberHandler = inviteMemberHandler;
    this.removeMemberHandler = removeMemberHandler;
  }

  @GetMapping
  List<CooperativeMemberResponse> list(@AuthenticationPrincipal UUID userId) {
    List<CooperativeMemberResult> results = listMembersHandler.handle(new ListMembersQuery(userId));
    return results.stream().map(CooperativeMemberResponse::from).toList();
  }

  @PostMapping("/invites")
  @ResponseStatus(HttpStatus.CREATED)
  CooperativeInviteResponse invite(
      @AuthenticationPrincipal UUID userId,
      Authentication authentication,
      @Valid @RequestBody InviteMemberRequest request) {
    CooperativeInviteResult result =
        inviteMemberHandler.handle(
            new InviteMemberCommand(userId, roleOf(authentication), request.email()));
    return CooperativeInviteResponse.from(result);
  }

  @DeleteMapping("/{targetUserId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void remove(
      @AuthenticationPrincipal UUID userId,
      Authentication authentication,
      @PathVariable UUID targetUserId) {
    removeMemberHandler.handle(
        new RemoveMemberCommand(userId, roleOf(authentication), targetUserId));
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
