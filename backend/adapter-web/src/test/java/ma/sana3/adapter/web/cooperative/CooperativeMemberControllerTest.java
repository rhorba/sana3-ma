package ma.sana3.adapter.web.cooperative;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ma.sana3.application.artisanprofile.CannotRemoveOwnerException;
import ma.sana3.application.artisanprofile.CooperativeInviteResult;
import ma.sana3.application.artisanprofile.CooperativeMemberResult;
import ma.sana3.application.artisanprofile.InviteMemberHandler;
import ma.sana3.application.artisanprofile.InviteeAlreadyMemberException;
import ma.sana3.application.artisanprofile.ListMembersHandler;
import ma.sana3.application.artisanprofile.NotCooperativeOwnerException;
import ma.sana3.application.artisanprofile.RemoveMemberHandler;
import ma.sana3.domain.artisanprofile.InviteStatus;
import ma.sana3.domain.artisanprofile.MembershipRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = CooperativeMemberController.class)
class CooperativeMemberControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ListMembersHandler listMembersHandler;
  @MockitoBean private InviteMemberHandler inviteMemberHandler;
  @MockitoBean private RemoveMemberHandler removeMemberHandler;

  private static RequestPostProcessor asArtisan(UUID userId) {
    return SecurityMockMvcRequestPostProcessors.authentication(
        new UsernamePasswordAuthenticationToken(
            userId, null, List.of(new SimpleGrantedAuthority("ROLE_ARTISAN"))));
  }

  @Test
  void listReturnsMembers() throws Exception {
    UUID userId = UUID.randomUUID();
    when(listMembersHandler.handle(any()))
        .thenReturn(
            List.of(
                new CooperativeMemberResult(
                    userId, "owner@example.com", MembershipRole.OWNER, Instant.now())));

    mockMvc
        .perform(get("/api/v1/artisan-profiles/me/members").with(asArtisan(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].email").value("owner@example.com"))
        .andExpect(jsonPath("$[0].role").value("OWNER"));
  }

  @Test
  void inviteCreatesAPendingInvite() throws Exception {
    UUID userId = UUID.randomUUID();
    when(inviteMemberHandler.handle(any()))
        .thenReturn(
            new CooperativeInviteResult(
                UUID.randomUUID(), UUID.randomUUID(), "Coop", InviteStatus.PENDING, Instant.now()));

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/members/invites")
                .with(csrf())
                .with(asArtisan(userId))
                .contentType("application/json")
                .content(
                    """
                    {"email":"invitee@example.com"}
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void inviteRejectsAlreadyMemberInvitee() throws Exception {
    UUID userId = UUID.randomUUID();
    when(inviteMemberHandler.handle(any())).thenThrow(new InviteeAlreadyMemberException());

    mockMvc
        .perform(
            post("/api/v1/artisan-profiles/me/members/invites")
                .with(csrf())
                .with(asArtisan(userId))
                .contentType("application/json")
                .content(
                    """
                    {"email":"invitee@example.com"}
                    """))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("INVITEE_ALREADY_MEMBER"));
  }

  @Test
  void removeSucceedsForOwner() throws Exception {
    UUID userId = UUID.randomUUID();

    mockMvc
        .perform(
            delete("/api/v1/artisan-profiles/me/members/" + UUID.randomUUID())
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isNoContent());
  }

  @Test
  void removeRejectsRemovingTheOwner() throws Exception {
    UUID userId = UUID.randomUUID();
    doThrow(new CannotRemoveOwnerException()).when(removeMemberHandler).handle(any());

    mockMvc
        .perform(
            delete("/api/v1/artisan-profiles/me/members/" + UUID.randomUUID())
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("CANNOT_REMOVE_OWNER"));
  }

  @Test
  void removeRejectsNonOwnerRemovingSomeoneElse() throws Exception {
    UUID userId = UUID.randomUUID();
    doThrow(new NotCooperativeOwnerException()).when(removeMemberHandler).handle(any());

    mockMvc
        .perform(
            delete("/api/v1/artisan-profiles/me/members/" + UUID.randomUUID())
                .with(csrf())
                .with(asArtisan(userId)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("NOT_COOPERATIVE_OWNER"));
  }
}
