import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideStore } from '@ngrx/store';
import { App } from './app';
import { routes } from './app.routes';
import { authFeatureKey, authReducer } from './store/auth/auth.reducer';
import { artisanProfileFeatureKey, artisanProfileReducer } from './store/artisan-profile/artisan-profile.reducer';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter(routes),
        provideStore({
          [authFeatureKey]: authReducer,
          [artisanProfileFeatureKey]: artisanProfileReducer,
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
