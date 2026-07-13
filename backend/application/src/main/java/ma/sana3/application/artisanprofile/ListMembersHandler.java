package ma.sana3.application.artisanprofile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import ma.sana3.domain.artisanprofile.CooperativeMembership;
import ma.sana3.domain.artisanprofile.CooperativeMembershipRepository;
import ma.sana3.domain.user.User;
import ma.sana3.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ListMembersHandler {

  private final CooperativeMembershipRepository membershipRepository;
  private final UserRepository userRepository;

  public ListMembersHandler(
      CooperativeMembershipRepository membershipRepository, UserRepository userRepository) {
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
  }

  public List<CooperativeMemberResult> handle(ListMembersQuery query) {
    var requesterMembership =
        membershipRepository
            .findByUserId(query.userId())
            .orElseThrow(ProfileNotFoundException::new);

    List<CooperativeMembership> members =
        membershipRepository.findByArtisanProfileId(requesterMembership.artisanProfileId());
    Map<UUID, User> usersById =
        userRepository
            .findByIds(members.stream().map(CooperativeMembership::userId).toList())
            .stream()
            .collect(Collectors.toMap(User::id, Function.identity()));

    return members.stream()
        .map(
            member ->
                new CooperativeMemberResult(
                    member.userId(),
                    usersById.get(member.userId()).email(),
                    member.role(),
                    member.joinedAt()))
        .toList();
  }
}
