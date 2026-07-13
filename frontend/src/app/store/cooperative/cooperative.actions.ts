import { createActionGroup, emptyProps, props } from '@ngrx/store';

import { CooperativeInviteResponse, CooperativeMemberResponse } from '../../core/cooperative/cooperative.models';

export const CooperativeActions = createActionGroup({
  source: 'Cooperative',
  events: {
    'Load Members': emptyProps(),
    'Load Members Success': props<{ members: CooperativeMemberResponse[] }>(),
    'Load Members Failure': props<{ message: string }>(),

    'Invite Member': props<{ email: string }>(),
    'Invite Member Success': props<{ invite: CooperativeInviteResponse }>(),
    'Invite Member Failure': props<{ message: string }>(),

    'Remove Member': props<{ targetUserId: string }>(),
    'Remove Member Success': props<{ targetUserId: string }>(),
    'Remove Member Failure': props<{ message: string }>(),

    'Load My Invites': emptyProps(),
    'Load My Invites Success': props<{ invites: CooperativeInviteResponse[] }>(),
    'Load My Invites Failure': props<{ message: string }>(),

    'Accept Invite': props<{ inviteId: string }>(),
    'Accept Invite Success': props<{ inviteId: string }>(),
    'Accept Invite Failure': props<{ message: string }>(),

    'Decline Invite': props<{ inviteId: string }>(),
    'Decline Invite Success': props<{ inviteId: string }>(),
    'Decline Invite Failure': props<{ message: string }>(),
  },
});
