import { Component, computed, effect, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Store } from '@ngrx/store';

import { AuthActions } from './store/auth/auth.actions';
import { selectIsAuthenticated, selectRole } from './store/auth/auth.selectors';
import { selectCartItemCount } from './store/cart/cart.selectors';
import { CooperativeActions } from './store/cooperative/cooperative.actions';
import { selectPendingInvites } from './store/cooperative/cooperative.selectors';

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
  private readonly role = this.store.selectSignal(selectRole);
  protected readonly isArtisan = computed(() => this.role() === 'ARTISAN');
  protected readonly cartItemCount = this.store.selectSignal(selectCartItemCount);
  protected readonly pendingInvites = this.store.selectSignal(selectPendingInvites);

  constructor() {
    // Pending invites must surface somewhere the invitee will actually notice them, not buried
    // in a settings page (Story 7.4) — a banner every ARTISAN sees on any page after login.
    effect(() => {
      if (this.isAuthenticated() && this.isArtisan()) {
        this.store.dispatch(CooperativeActions.loadMyInvites());
      }
    });
  }

  acceptInvite(inviteId: string): void {
    this.store.dispatch(CooperativeActions.acceptInvite({ inviteId }));
  }

  declineInvite(inviteId: string): void {
    this.store.dispatch(CooperativeActions.declineInvite({ inviteId }));
  }

  logout(): void {
    this.store.dispatch(AuthActions.logout());
    this.router.navigateByUrl('/');
  }
}
