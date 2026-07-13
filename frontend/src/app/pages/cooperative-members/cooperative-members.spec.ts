import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { Action } from '@ngrx/store';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Observable, Subject } from 'rxjs';

import { CooperativeMemberResponse } from '../../core/cooperative/cooperative.models';
import { selectUserId } from '../../store/auth/auth.selectors';
import { CooperativeActions } from '../../store/cooperative/cooperative.actions';
import {
  selectInviteSending,
  selectMembers,
  selectMembersError,
  selectMembersLoaded,
  selectMembersLoading,
} from '../../store/cooperative/cooperative.selectors';
import { CooperativeMembers } from './cooperative-members';

describe('CooperativeMembers', () => {
  let component: CooperativeMembers;
  let fixture: ComponentFixture<CooperativeMembers>;
  let store: MockStore;
  let actions$: Subject<Action>;

  const owner: CooperativeMemberResponse = {
    userId: 'owner-1',
    email: 'owner@example.com',
    role: 'OWNER',
    joinedAt: '2026-01-01T00:00:00Z',
  };
  const memberRow: CooperativeMemberResponse = {
    userId: 'member-1',
    email: 'member@example.com',
    role: 'MEMBER',
    joinedAt: '2026-01-02T00:00:00Z',
  };

  beforeEach(async () => {
    actions$ = new Subject<Action>();

    await TestBed.configureTestingModule({
      imports: [CooperativeMembers],
      providers: [
        provideNoopAnimations(),
        provideMockActions((): Observable<Action> => actions$),
        provideMockStore({
          selectors: [
            { selector: selectMembers, value: [owner, memberRow] },
            { selector: selectMembersLoading, value: false },
            { selector: selectMembersLoaded, value: true },
            { selector: selectMembersError, value: null },
            { selector: selectInviteSending, value: false },
            { selector: selectUserId, value: owner.userId },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(CooperativeMembers);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and dispatch loadMembers on construction', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(CooperativeActions.loadMembers());
  });

  it('renders a card per member with role and shows the invite form for the owner', () => {
    createComponent();

    const cards = fixture.nativeElement.querySelectorAll('.member-card');
    expect(cards.length).toBe(2);
    expect(fixture.nativeElement.querySelector('.invite-form')).not.toBeNull();
  });

  it('owner can remove a MEMBER but not another OWNER row', () => {
    createComponent();

    expect(component.canRemove(memberRow)).toBe(true);
    expect(component.canRemove(owner)).toBe(false);
  });

  it('invite dispatches inviteMember with the form value when valid', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    component.inviteForm.setValue({ email: 'invitee@example.com' });

    component.invite();

    expect(dispatchSpy).toHaveBeenCalledWith(CooperativeActions.inviteMember({ email: 'invitee@example.com' }));
  });

  it('invite does not dispatch when the form is invalid', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    component.inviteForm.setValue({ email: 'not-an-email' });

    component.invite();

    expect(dispatchSpy).not.toHaveBeenCalledWith(expect.objectContaining({ type: '[Cooperative] Invite Member' }));
  });

  it('remove dispatches removeMember when confirmed', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.remove(memberRow);

    expect(dispatchSpy).toHaveBeenCalledWith(
      CooperativeActions.removeMember({ targetUserId: memberRow.userId }),
    );
  });

  it('shows a snackbar with the backend message when inviteMemberFailure fires', () => {
    createComponent();

    actions$.next(CooperativeActions.inviteMemberFailure({ message: 'That user already belongs to a cooperative' }));
    fixture.detectChanges();

    expect(document.body.textContent).toContain('That user already belongs to a cooperative');
  });
});
