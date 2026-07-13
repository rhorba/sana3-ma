package ma.sana3.adapter.web.cooperative;

import java.util.List;
import java.util.UUID;
import ma.sana3.application.artisanprofile.AcceptInviteHandler;
import ma.sana3.application.artisanprofile.CooperativeInviteResult;
import ma.sana3.application.artisanprofile.DeclineInviteHandler;
import ma.sana3.application.artisanprofile.ListMyInvitesHandler;
import ma.sana3.application.artisanprofile.ListMyInvitesQuery;
import ma.sana3.application.artisanprofile.RespondToInviteCommand;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cooperative-invites/me")
class CooperativeInviteController {

  private final ListMyInvitesHandler listMyInvitesHandler;
  private final AcceptInviteHandler acceptInviteHandler;
  private final DeclineInviteHandler declineInviteHandler;

  CooperativeInviteController(
      ListMyInvitesHandler listMyInvitesHandler,
      AcceptInviteHandler acceptInviteHandler,
      DeclineInviteHandler declineInviteHandler) {
    this.listMyInvitesHandler = listMyInvitesHandler;
    this.acceptInviteHandler = acceptInviteHandler;
    this.declineInviteHandler = declineInviteHandler;
  }

  @GetMapping
  List<CooperativeInviteResponse> list(@AuthenticationPrincipal UUID userId) {
    List<CooperativeInviteResult> results =
        listMyInvitesHandler.handle(new ListMyInvitesQuery(userId));
    return results.stream().map(CooperativeInviteResponse::from).toList();
  }

  @PostMapping("/{inviteId}/accept")
  CooperativeInviteResponse accept(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID inviteId) {
    CooperativeInviteResult result =
        acceptInviteHandler.handle(new RespondToInviteCommand(userId, inviteId));
    return CooperativeInviteResponse.from(result);
  }

  @PostMapping("/{inviteId}/decline")
  CooperativeInviteResponse decline(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID inviteId) {
    CooperativeInviteResult result =
        declineInviteHandler.handle(new RespondToInviteCommand(userId, inviteId));
    return CooperativeInviteResponse.from(result);
  }
}
