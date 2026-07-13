import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideStore } from '@ngrx/store';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { App } from './app';
import { routes } from './app.routes';
import { authFeatureKey, authReducer } from './store/auth/auth.reducer';
import { artisanProfileFeatureKey, artisanProfileReducer } from './store/artisan-profile/artisan-profile.reducer';
import { cartFeatureKey, cartReducer } from './store/cart/cart.reducer';
import { selectIsAuthenticated, selectRole } from './store/auth/auth.selectors';
import { selectCartItemCount } from './store/cart/cart.selectors';
import { cooperativeFeatureKey, cooperativeReducer } from './store/cooperative/cooperative.reducer';
import { selectPendingInvites } from './store/cooperative/cooperative.selectors';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter(routes),
        provideStore({
          [authFeatureKey]: authReducer,
          [artisanProfileFeatureKey]: artisanProfileReducer,
          [cartFeatureKey]: cartReducer,
          [cooperativeFeatureKey]: cooperativeReducer,
        }),
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render the toolbar title', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('mat-toolbar')?.textContent).toContain('Sana3.ma');
  });
});

describe('App invite banner', () => {
  let store: MockStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter(routes),
        provideMockStore({
          selectors: [
            { selector: selectIsAuthenticated, value: true },
            { selector: selectRole, value: 'ARTISAN' },
            { selector: selectCartItemCount, value: 0 },
            {
              selector: selectPendingInvites,
              value: [
                {
                  id: 'invite-1',
                  artisanProfileId: 'profile-1',
                  artisanDisplayName: 'Coop Atlas',
                  status: 'PENDING',
                  createdAt: '2026-01-01T00:00:00Z',
                },
              ],
            },
          ],
        }),
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
  });

  it('shows a banner for a pending invite with accept/decline actions', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const banner = fixture.nativeElement.querySelector('.invite-banner');
    expect(banner?.textContent).toContain('Coop Atlas invited you to join.');
  });

  it('acceptInvite dispatches acceptInvite', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const dispatchSpy = vi.spyOn(store, 'dispatch');

    fixture.componentInstance.acceptInvite('invite-1');

    expect(dispatchSpy).toHaveBeenCalledWith({
      type: '[Cooperative] Accept Invite',
      inviteId: 'invite-1',
    });
  });
});
