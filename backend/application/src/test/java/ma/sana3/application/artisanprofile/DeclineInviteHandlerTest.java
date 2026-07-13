package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.CooperativeInvite;
import ma.sana3.domain.artisanprofile.CooperativeInviteRepository;
import ma.sana3.domain.artisanprofile.InviteStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeclineInviteHandlerTest {

  @Mock private CooperativeInviteRepository inviteRepository;

  private DeclineInviteHandler handler;

  @BeforeEach
  void setUp() {
    handler = new DeclineInviteHandler(inviteRepository);
  }

  @Test
  void declineMarksInviteDeclined() {
    UUID userId = UUID.randomUUID();
    CooperativeInvite invite = CooperativeInvite.create(UUID.randomUUID(), userId);
    when(inviteRepository.findById(invite.id())).thenReturn(Optional.of(invite));
    when(inviteRepository.save(any(CooperativeInvite.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CooperativeInviteResult result =
        handler.handle(new RespondToInviteCommand(userId, invite.id()));

    assertEquals(InviteStatus.DECLINED, result.status());
  }

  @Test
  void rejectsInviteBelongingToAnotherUser() {
    UUID userId = UUID.randomUUID();
    CooperativeInvite invite = CooperativeInvite.create(UUID.randomUUID(), UUID.randomUUID());
    when(inviteRepository.findById(invite.id())).thenReturn(Optional.of(invite));

    assertThrows(
        InviteNotFoundException.class,
        () -> handler.handle(new RespondToInviteCommand(userId, invite.id())));
  }
}
