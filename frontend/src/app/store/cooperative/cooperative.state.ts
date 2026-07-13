import { CooperativeInviteResponse, CooperativeMemberResponse } from '../../core/cooperative/cooperative.models';

export interface CooperativeState {
  members: CooperativeMemberResponse[];
  membersLoading: boolean;
  membersLoaded: boolean;
  membersError: string | null;

  inviteSending: boolean;
  inviteError: string | null;

  pendingInvites: CooperativeInviteResponse[];
  invitesLoading: boolean;
  invitesLoaded: boolean;
  invitesError: string | null;

  respondingInviteId: string | null;
}

export const initialCooperativeState: CooperativeState = {
  members: [],
  membersLoading: false,
  membersLoaded: false,
  membersError: null,

  inviteSending: false,
  inviteError: null,

  pendingInvites: [],
  invitesLoading: false,
  invitesLoaded: false,
  invitesError: null,

  respondingInviteId: null,
};
