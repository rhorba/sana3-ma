import { CooperativeActions } from './cooperative.actions';
import { cooperativeReducer } from './cooperative.reducer';
import { initialCooperativeState } from './cooperative.state';

describe('cooperativeReducer', () => {
  const member = {
    userId: 'user-1',
    email: 'owner@example.com',
    role: 'OWNER' as const,
    joinedAt: '2026-01-01T00:00:00Z',
  };

  const invite = {
    id: 'invite-1',
    artisanProfileId: 'profile-1',
    artisanDisplayName: 'Coop Atlas',
    status: 'PENDING' as const,
    createdAt: '2026-01-01T00:00:00Z',
  };

  it('sets membersLoading on loadMembers', () => {
    const state = cooperativeReducer(initialCooperativeState, CooperativeActions.loadMembers());
    expect(state.membersLoading).toBe(true);
  });

  it('populates members and marks loaded on loadMembersSuccess', () => {
    const state = cooperativeReducer(
      initialCooperativeState,
      CooperativeActions.loadMembersSuccess({ members: [member] }),
    );
    expect(state.members).toEqual([member]);
    expect(state.membersLoading).toBe(false);
    expect(state.membersLoaded).toBe(true);
  });

  it('records an error message on loadMembersFailure', () => {
    const state = cooperativeReducer(
      initialCooperativeState,
      CooperativeActions.loadMembersFailure({ message: 'boom' }),
    );
    expect(state.membersError).toBe('boom');
    expect(state.membersLoading).toBe(false);
  });

  it('removes a member from state on removeMemberSuccess', () => {
    const withMembers = { ...initialCooperativeState, members: [member] };
    const state = cooperativeReducer(
      withMembers,
      CooperativeActions.removeMemberSuccess({ targetUserId: member.userId }),
    );
    expect(state.members).toEqual([]);
  });

  it('sets inviteSending on inviteMember and clears it on success', () => {
    const sending = cooperativeReducer(initialCooperativeState, CooperativeActions.inviteMember({ email: 'a@b.com' }));
    expect(sending.inviteSending).toBe(true);

    const done = cooperativeReducer(sending, CooperativeActions.inviteMemberSuccess({ invite }));
    expect(done.inviteSending).toBe(false);
  });

  it('populates pendingInvites on loadMyInvitesSuccess', () => {
    const state = cooperativeReducer(
      initialCooperativeState,
      CooperativeActions.loadMyInvitesSuccess({ invites: [invite] }),
    );
    expect(state.pendingInvites).toEqual([invite]);
    expect(state.invitesLoaded).toBe(true);
  });

  it('removes the invite from pendingInvites on acceptInviteSuccess', () => {
    const withInvites = { ...initialCooperativeState, pendingInvites: [invite] };
    const state = cooperativeReducer(
      withInvites,
      CooperativeActions.acceptInviteSuccess({ inviteId: invite.id }),
    );
    expect(state.pendingInvites).toEqual([]);
  });

  it('removes the invite from pendingInvites on declineInviteSuccess', () => {
    const withInvites = { ...initialCooperativeState, pendingInvites: [invite] };
    const state = cooperativeReducer(
      withInvites,
      CooperativeActions.declineInviteSuccess({ inviteId: invite.id }),
    );
    expect(state.pendingInvites).toEqual([]);
  });

  it('tracks respondingInviteId while an accept/decline is in flight', () => {
    const state = cooperativeReducer(
      initialCooperativeState,
      CooperativeActions.acceptInvite({ inviteId: invite.id }),
    );
    expect(state.respondingInviteId).toBe(invite.id);

    const cleared = cooperativeReducer(
      state,
      CooperativeActions.acceptInviteFailure({ message: 'boom' }),
    );
    expect(cleared.respondingInviteId).toBeNull();
  });
});
