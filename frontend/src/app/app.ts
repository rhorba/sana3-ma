import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Store } from '@ngrx/store';

import { AuthActions } from './store/auth/auth.actions';
import { selectIsAuthenticated } from './store/auth/auth.selectors';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, MatToolbarModule, MatButtonModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  private readonly store = inject(Store);
  private readonly router = inject(Router);

  protected readonly title = signal('Sana3.ma');
  protected readonly isAuthenticated = this.store.selectSignal(selectIsAuthenticated);

  logout(): void {
    this.store.dispatch(AuthActions.logout());
    this.router.navigateByUrl('/');
  }
}
