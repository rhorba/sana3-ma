package ma.sana3.application.artisanprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.artisanprofile.MembershipRole;
import ma.sana3.domain.user.Role;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListMembersHandlerTest {

  @Mock private CooperativeMembershipRepository membershipRepository;
  @Mock private UserRepository userRepository;

  private ListMembersHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ListMembersHandler(membershipRepository, userRepository);
  }

  @Test
  void returnsAllMembersWithEmailAndRole() {
    UUID requesterId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    UUID otherMemberId = UUID.randomUUID();
    CooperativeMembership owner =
        CooperativeMembership.create(requesterId, profileId, MembershipRole.OWNER);
    CooperativeMembership member =
        CooperativeMembership.create(otherMemberId, profileId, MembershipRole.MEMBER);
    when(membershipRepository.findByUserId(requesterId)).thenReturn(Optional.of(owner));
    when(membershipRepository.findByArtisanProfileId(profileId)).thenReturn(List.of(owner, member));
    User ownerUser =
        new User(
            requesterId, "owner@example.com", "hash", Role.ARTISAN, Instant.now(), Instant.now());
    User memberUser =
        new User(
            otherMemberId,
            "member@example.com",
            "hash",
            Role.ARTISAN,
            Instant.now(),
            Instant.now());
    when(userRepository.findByIds(List.of(requesterId, otherMemberId)))
        .thenReturn(List.of(ownerUser, memberUser));

    List<CooperativeMemberResult> results = handler.handle(new ListMembersQuery(requesterId));

    assertEquals(2, results.size());
  }

  @Test
  void rejectsUserWithoutMembership() {
    UUID requesterId = UUID.randomUUID();
    when(membershipRepository.findByUserId(requesterId)).thenReturn(Optional.empty());

    assertThrows(
        ProfileNotFoundException.class, () -> handler.handle(new ListMembersQuery(requesterId)));
  }
}
