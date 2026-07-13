import { createFeature, createReducer, on } from '@ngrx/store';

import { CooperativeActions } from './cooperative.actions';
import { CooperativeState, initialCooperativeState } from './cooperative.state';

export const cooperativeFeature = createFeature({
  name: 'cooperative',
  reducer: createReducer(
    initialCooperativeState,
    on(CooperativeActions.loadMembers, (state): CooperativeState => ({
      ...state,
      membersLoading: true,
      membersError: null,
    })),
    on(CooperativeActions.loadMembersSuccess, (state, { members }): CooperativeState => ({
      ...state,
      members,
      membersLoading: false,
      membersLoaded: true,
    })),
    on(CooperativeActions.loadMembersFailure, (state, { message }): CooperativeState => ({
      ...state,
      membersLoading: false,
      membersError: message,
    })),

    on(CooperativeActions.inviteMember, (state): CooperativeState => ({
      ...state,
      inviteSending: true,
      inviteError: null,
    })),
    on(CooperativeActions.inviteMemberSuccess, (state): CooperativeState => ({
      ...state,
      inviteSending: false,
    })),
    on(CooperativeActions.inviteMemberFailure, (state, { message }): CooperativeState => ({
      ...state,
      inviteSending: false,
      inviteError: message,
    })),

    on(CooperativeActions.removeMemberSuccess, (state, { targetUserId }): CooperativeState => ({
      ...state,
      members: state.members.filter((member) => member.userId !== targetUserId),
    })),

    on(CooperativeActions.loadMyInvites, (state): CooperativeState => ({
      ...state,
      invitesLoading: true,
      invitesError: null,
    })),
    on(CooperativeActions.loadMyInvitesSuccess, (state, { invites }): CooperativeState => ({
      ...state,
      pendingInvites: invites,
      invitesLoading: false,
      invitesLoaded: true,
    })),
    on(CooperativeActions.loadMyInvitesFailure, (state, { message }): CooperativeState => ({
      ...state,
      invitesLoading: false,
      invitesError: message,
    })),

    on(CooperativeActions.acceptInvite, CooperativeActions.declineInvite, (state, { inviteId }): CooperativeState => ({
      ...state,
      respondingInviteId: inviteId,
    })),
    on(
      CooperativeActions.acceptInviteSuccess,
      CooperativeActions.declineInviteSuccess,
      (state, { inviteId }): CooperativeState => ({
        ...state,
        pendingInvites: state.pendingInvites.filter((invite) => invite.id !== inviteId),
        respondingInviteId: null,
      }),
    ),
    on(
      CooperativeActions.acceptInviteFailure,
      CooperativeActions.declineInviteFailure,
      (state): CooperativeState => ({
        ...state,
        respondingInviteId: null,
      }),
    ),
  ),
});

export const {
  name: cooperativeFeatureKey,
  reducer: cooperativeReducer,
  selectMembers,
  selectMembersLoading,
  selectMembersLoaded,
  selectMembersError,
  selectInviteSending,
  selectInviteError,
  selectPendingInvites,
  selectInvitesLoading,
  selectInvitesLoaded,
  selectInvitesError,
  selectRespondingInviteId,
} = cooperativeFeature;
