import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

import { CooperativeService } from '../../core/cooperative/cooperative.service';
import { extractErrorMessage } from '../../core/http-error.util';
import { CooperativeActions } from './cooperative.actions';

@Injectable()
export class CooperativeEffects {
  private readonly actions$ = inject(Actions);
  private readonly cooperativeService = inject(CooperativeService);

  loadMembers$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CooperativeActions.loadMembers),
      switchMap(() =>
        this.cooperativeService.getMembers().pipe(
          map((members) => CooperativeActions.loadMembersSuccess({ members })),
          catchError((error) =>
            of(
              CooperativeActions.loadMembersFailure({
                message: extractErrorMessage(error, "Couldn't load cooperative members."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  inviteMember$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CooperativeActions.inviteMember),
      switchMap(({ email }) =>
        this.cooperativeService.inviteMember({ email }).pipe(
          map((invite) => CooperativeActions.inviteMemberSuccess({ invite })),
          catchError((error) =>
            of(
              CooperativeActions.inviteMemberFailure({
                message: extractErrorMessage(error, "Couldn't send the invite."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  reloadMembersAfterInvite$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CooperativeActions.inviteMemberSuccess),
      map(() => CooperativeActions.loadMembers()),
    ),
  );

  removeMember$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CooperativeActions.removeMember),
      switchMap(({ targetUserId }) =>
        this.cooperativeService.removeMember(targetUserId).pipe(
          map(() => CooperativeActions.removeMemberSuccess({ targetUserId })),
          catchError((error) =>
            of(
              CooperativeActions.removeMemberFailure({
                message: extractErrorMessage(error, "Couldn't remove that member."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  loadMyInvites$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CooperativeActions.loadMyInvites),
      switchMap(() =>
        this.cooperativeService.getMyInvites().pipe(
          map((invites) => CooperativeActions.loadMyInvitesSuccess({ invites })),
          catchError((error) =>
            of(
              CooperativeActions.loadMyInvitesFailure({
                message: extractErrorMessage(error, "Couldn't load your invites."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  acceptInvite$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CooperativeActions.acceptInvite),
      switchMap(({ inviteId }) =>
        this.cooperativeService.acceptInvite(inviteId).pipe(
          map(() => CooperativeActions.acceptInviteSuccess({ inviteId })),
          catchError((error) =>
            of(
              CooperativeActions.acceptInviteFailure({
                message: extractErrorMessage(error, "Couldn't accept that invite."),
              }),
            ),
          ),
        ),
      ),
    ),
  );

  declineInvite$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CooperativeActions.declineInvite),
      switchMap(({ inviteId }) =>
        this.cooperativeService.declineInvite(inviteId).pipe(
          map(() => CooperativeActions.declineInviteSuccess({ inviteId })),
          catchError((error) =>
            of(
              CooperativeActions.declineInviteFailure({
                message: extractErrorMessage(error, "Couldn't decline that invite."),
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
