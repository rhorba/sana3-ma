package ma.sana3.adapter.web.cooperative;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.artisanprofile.AcceptInviteHandler;
import ma.sana3.application.artisanprofile.CooperativeInviteResult;
import ma.sana3.application.artisanprofile.DeclineInviteHandler;
import ma.sana3.application.artisanprofile.InviteNotFoundException;
import ma.sana3.application.artisanprofile.ListMyInvitesHandler;
import ma.sana3.domain.artisanprofile.IllegalInviteStatusTransitionException;
import ma.sana3.domain.artisanprofile.InviteStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = CooperativeInviteController.class)
class CooperativeInviteControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ListMyInvitesHandler listMyInvitesHandler;
  @MockitoBean private AcceptInviteHandler acceptInviteHandler;
  @MockitoBean private DeclineInviteHandler declineInviteHandler;

  private static RequestPostProcessor asArtisan(UUID userId) {
    return SecurityMockMvcRequestPostProcessors.authentication(
        new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority("ROLE_ARTISAN"))));
  }

  @Test
  void listReturnsPendingInvites() throws Exception {
    UUID userId = UUID.randomUUID();
    when(listMyInvitesHandler.handle(any()))
        .thenReturn(
            List.of(
                new CooperativeInviteResult(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "Coop Name",
                    InviteStatus.PENDING,
                    Instant.now())));

    mockMvc
        .perform(get("/api/v1/cooperative-invites/me").with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].artisanDisplayName").value("Coop Name"));
  }

  @Test
  void acceptReturnsAcceptedInvite() throws Exception {
    UUID userId = UUID.randomUUID();
    when(acceptInviteHandler.handle(any()))
        .thenReturn(
            new CooperativeInviteResult(
                UUID.randomUUID(), UUID.randomUUID(), null, InviteStatus.ACCEPTED, Instant.now()));

    mockMvc
        .perform(
            post("/api/v1/cooperative-invites/me/" + UUID.randomUUID() + "/accept")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ACCEPTED"));
  }

  @Test
  void acceptReturnsNotFoundForSomeoneElsesInvite() throws Exception {
    UUID userId = UUID.randomUUID();
    when(acceptInviteHandler.handle(any())).thenThrow(new InviteNotFoundException());

    mockMvc
        .perform(
            post("/api/v1/cooperative-invites/me/" + UUID.randomUUID() + "/accept")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("INVITE_NOT_FOUND"));
  }

  @Test
  void declineReturnsDeclinedInvite() throws Exception {
    UUID userId = UUID.randomUUID();
    when(declineInviteHandler.handle(any()))
        .thenReturn(
            new CooperativeInviteResult(
                UUID.randomUUID(), UUID.randomUUID(), null, InviteStatus.DECLINED, Instant.now()));

    mockMvc
        .perform(
            post("/api/v1/cooperative-invites/me/" + UUID.randomUUID() + "/decline")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DECLINED"));
  }

  @Test
  void acceptRejectsAlreadyResolvedInvite() throws Exception {
    UUID userId = UUID.randomUUID();
    when(acceptInviteHandler.handle(any()))
        .thenThrow(
            new IllegalInviteStatusTransitionException(
                InviteStatus.DECLINED, InviteStatus.ACCEPTED));

    mockMvc
        .perform(
            post("/api/v1/cooperative-invites/me/" + UUID.randomUUID() + "/accept")
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ILLEGAL_INVITE_STATUS_TRANSITION"));
  }
}
