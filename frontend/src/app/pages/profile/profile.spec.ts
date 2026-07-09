import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { Action } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';

import { ArtisanProfileActions } from '../../store/artisan-profile/artisan-profile.actions';
import {
  selectHasProfile,
  selectProfileError,
  selectProfileForm,
  selectProfileLoaded,
  selectProfileLoading,
  selectProfileSaving,
} from '../../store/artisan-profile/artisan-profile.selectors';
import { Profile } from './profile';

describe('Profile', () => {
  let component: Profile;
  let fixture: ComponentFixture<Profile>;
  let store: MockStore;
  let actions$: Subject<Action>;

  beforeEach(async () => {
    actions$ = new Subject<Action>();

    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        provideNoopAnimations(),
        provideMockActions((): Observable<Action> => actions$),
        provideMockStore({
          selectors: [
            { selector: selectProfileLoading, value: false },
            { selector: selectProfileLoaded, value: false },
            { selector: selectProfileSaving, value: false },
            { selector: selectProfileError, value: null },
            { selector: selectHasProfile, value: false },
            {
              selector: selectProfileForm,
              value: { displayName: '', craftType: '', region: '', bio: '', contactPhone: '' },
            },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create and dispatch loadProfile on construction', () => {
    const dispatchSpy = vi.spyOn(store, 'dispatch');
    createComponent();

    expect(component).toBeTruthy();
    expect(dispatchSpy).toHaveBeenCalledWith(ArtisanProfileActions.loadProfile());
  });

  it('shows a loading message and hides the form while loading', () => {
    store.overrideSelector(selectProfileLoading, true);
    createComponent();

    expect(fixture.nativeElement.textContent).toContain('Loading your profile');
    expect(fixture.nativeElement.querySelector('form')).toBeNull();
  });

  it('shows the empty-state prompt when loaded with no existing profile', () => {
    store.overrideSelector(selectProfileLoaded, true);
    store.overrideSelector(selectHasProfile, false);
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).not.toBeNull();
  });

  it('does not show the empty-state prompt when a profile already exists', () => {
    store.overrideSelector(selectProfileLoaded, true);
    store.overrideSelector(selectHasProfile, true);
    createComponent();

    expect(fixture.nativeElement.querySelector('.empty-state-prompt')).toBeNull();
  });

  it('pre-fills the form from the store', () => {
    store.overrideSelector(selectProfileForm, {
      displayName: 'Yassine Zellige',
      craftType: 'Zellige tiling',
      region: 'Fes',
      bio: 'Third-generation artisan.',
      contactPhone: '+212600000000',
    });
    createComponent();

    expect(component.form.getRawValue()).toEqual({
      displayName: 'Yassine Zellige',
      craftType: 'Zellige tiling',
      region: 'Fes',
      bio: 'Third-generation artisan.',
      contactPhone: '+212600000000',
    });
  });

  it('shows inline validation errors and does not dispatch when required fields are blank', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(dispatchSpy).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelectorAll('mat-error').length).toBeGreaterThan(0);
  });

  it('dispatches saveProfile with the form value when valid', () => {
    createComponent();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    component.form.setValue({
      displayName: 'Yassine Zellige',
      craftType: 'Zellige tiling',
      region: 'Fes',
      bio: 'Third-generation artisan.',
      contactPhone: '+212600000000',
    });
    fixture.nativeElement.querySelector('form').dispatchEvent(new Event('submit'));

    expect(dispatchSpy).toHaveBeenCalledWith(
      ArtisanProfileActions.saveProfile({
        displayName: 'Yassine Zellige',
        craftType: 'Zellige tiling',
        region: 'Fes',
        bio: 'Third-generation artisan.',
        contactPhone: '+212600000000',
      }),
    );
  });

  it('shows a success snackbar when saveProfileSuccess fires', () => {
    createComponent();

    actions$.next(
      ArtisanProfileActions.saveProfileSuccess({
        response: {
          id: 'profile-1',
          userId: 'user-1',
          displayName: 'Yassine Zellige',
          craftType: 'Zellige tiling',
          region: 'Fes',
          bio: 'Third-generation artisan.',
          contactPhone: '+212600000000',
          createdAt: '2026-01-01T00:00:00Z',
          updatedAt: '2026-01-02T00:00:00Z',
        },
      }),
    );
    fixture.detectChanges();

    expect(document.body.textContent).toContain('Profile updated');
  });
});
