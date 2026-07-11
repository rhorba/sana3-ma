import { ApplicationConfig, inject, isDevMode, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideStore, Store } from '@ngrx/store';
import { Actions, ofType, provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { firstValueFrom } from 'rxjs';
import { take } from 'rxjs/operators';

import { routes } from './app.routes';
import { authInterceptor } from './core/auth/auth.interceptor';
import { AuthActions } from './store/auth/auth.actions';
import { authFeatureKey, authReducer } from './store/auth/auth.reducer';
import { AuthEffects } from './store/auth/auth.effects';
import { artisanProfileFeatureKey, artisanProfileReducer } from './store/artisan-profile/artisan-profile.reducer';
import { ArtisanProfileEffects } from './store/artisan-profile/artisan-profile.effects';
import { catalogFeatureKey, catalogReducer } from './store/catalog/catalog.reducer';
import { CatalogEffects } from './store/catalog/catalog.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideStore({
      [authFeatureKey]: authReducer,
      [artisanProfileFeatureKey]: artisanProfileReducer,
      [catalogFeatureKey]: catalogReducer,
    }),
    provideEffects([AuthEffects, ArtisanProfileEffects, CatalogEffects]),
    provideStoreDevtools({ maxAge: 25, logOnly: !isDevMode() }),
    // Restore a session from the httpOnly refresh cookie (if any) before the app renders,
    // so route guards don't redirect an already-logged-in user to /login on page reload.
    provideAppInitializer(async () => {
      const store = inject(Store);
      const actions$ = inject(Actions);
      store.dispatch(AuthActions.refreshToken());
      await firstValueFrom(
        actions$.pipe(ofType(AuthActions.refreshTokenSuccess, AuthActions.refreshTokenFailure), take(1)),
      );
    }),
  ]
};
