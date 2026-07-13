import { cooperativeFeature } from './cooperative.reducer';

export const {
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
