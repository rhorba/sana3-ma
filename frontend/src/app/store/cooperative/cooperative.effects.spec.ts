import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Action } from '@ngrx/store';
import { Observable, firstValueFrom, of, throwError } from 'rxjs';

import { CooperativeService } from '../../core/cooperative/cooperative.service';
import { CooperativeActions } from './cooperative.actions';
import { CooperativeEffects } from './cooperative.effects';

describe('CooperativeEffects', () => {
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

  let actions$: Observable<Action>;
  let cooperativeService: {
    getMembers: ReturnType<typeof vi.fn>;
    inviteMember: ReturnType<typeof vi.fn>;
    removeMember: ReturnType<typeof vi.fn>;
    getMyInvites: ReturnType<typeof vi.fn>;
    acceptInvite: ReturnType<typeof vi.fn>;
    declineInvite: ReturnType<typeof vi.fn>;
  };
  let effects: CooperativeEffects;

  function setup(): void {
    TestBed.configureTestingModule({
      providers: [
        CooperativeEffects,
        provideMockActions(() => actions$),
        { provide: CooperativeService, useValue: cooperativeService },
      ],
    });
    effects = TestBed.inject(CooperativeEffects);
  }

  beforeEach(() => {
    cooperativeService = {
      getMembers: vi.fn(),
      inviteMember: vi.fn(),
      removeMember: vi.fn(),
      getMyInvites: vi.fn(),
      acceptInvite: vi.fn(),
      declineInvite: vi.fn(),
    };
  });

  it('loadMembers$ maps a successful fetch to loadMembersSuccess', async () => {
    cooperativeService.getMembers.mockReturnValue(of([member]));
    actions$ = of(CooperativeActions.loadMembers());
    setup();

    const result = await firstValueFrom(effects.loadMembers$);

    expect(result).toEqual(CooperativeActions.loadMembersSuccess({ members: [member] }));
  });

  it('loadMembers$ falls back to a generic message on failure', async () => {
    cooperativeService.getMembers.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 500 })));
    actions$ = of(CooperativeActions.loadMembers());
    setup();

    const result = await firstValueFrom(effects.loadMembers$);

    expect(result).toEqual(
      CooperativeActions.loadMembersFailure({ message: "Couldn't load cooperative members." }),
    );
  });

  it('inviteMember$ maps a successful invite to inviteMemberSuccess', async () => {
    cooperativeService.inviteMember.mockReturnValue(of(invite));
    actions$ = of(CooperativeActions.inviteMember({ email: 'invitee@example.com' }));
    setup();

    const result = await firstValueFrom(effects.inviteMember$);

    expect(cooperativeService.inviteMember).toHaveBeenCalledWith({ email: 'invitee@example.com' });
    expect(result).toEqual(CooperativeActions.inviteMemberSuccess({ invite }));
  });

  it('inviteMember$ maps a backend error to inviteMemberFailure with its message', async () => {
    cooperativeService.inviteMember.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            error: { error: { code: 'INVITEE_ALREADY_MEMBER', message: 'That user already belongs to a cooperative', details: [] } },
          }),
      ),
    );
    actions$ = of(CooperativeActions.inviteMember({ email: 'invitee@example.com' }));
    setup();

    const result = await firstValueFrom(effects.inviteMember$);

    expect(result).toEqual(
      CooperativeActions.inviteMemberFailure({ message: 'That user already belongs to a cooperative' }),
    );
  });

  it('reloadMembersAfterInvite$ dispatches loadMembers after inviteMemberSuccess', async () => {
    actions$ = of(CooperativeActions.inviteMemberSuccess({ invite }));
    setup();

    const result = await firstValueFrom(effects.reloadMembersAfterInvite$);

    expect(result).toEqual(CooperativeActions.loadMembers());
  });

  it('removeMember$ maps a successful removal to removeMemberSuccess', async () => {
    cooperativeService.removeMember.mockReturnValue(of(undefined));
    actions$ = of(CooperativeActions.removeMember({ targetUserId: 'user-2' }));
    setup();

    const result = await firstValueFrom(effects.removeMember$);

    expect(result).toEqual(CooperativeActions.removeMemberSuccess({ targetUserId: 'user-2' }));
  });

  it('loadMyInvites$ maps a successful fetch to loadMyInvitesSuccess', async () => {
    cooperativeService.getMyInvites.mockReturnValue(of([invite]));
    actions$ = of(CooperativeActions.loadMyInvites());
    setup();

    const result = await firstValueFrom(effects.loadMyInvites$);

    expect(result).toEqual(CooperativeActions.loadMyInvitesSuccess({ invites: [invite] }));
  });

  it('acceptInvite$ maps a successful accept to acceptInviteSuccess', async () => {
    cooperativeService.acceptInvite.mockReturnValue(of({ ...invite, status: 'ACCEPTED' }));
    actions$ = of(CooperativeActions.acceptInvite({ inviteId: 'invite-1' }));
    setup();

    const result = await firstValueFrom(effects.acceptInvite$);

    expect(result).toEqual(CooperativeActions.acceptInviteSuccess({ inviteId: 'invite-1' }));
  });

  it('declineInvite$ maps a successful decline to declineInviteSuccess', async () => {
    cooperativeService.declineInvite.mockReturnValue(of({ ...invite, status: 'DECLINED' }));
    actions$ = of(CooperativeActions.declineInvite({ inviteId: 'invite-1' }));
    setup();

    const result = await firstValueFrom(effects.declineInvite$);

    expect(result).toEqual(CooperativeActions.declineInviteSuccess({ inviteId: 'invite-1' }));
  });
});
