import { Routes } from '@angular/router';

import { artisanGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/home/home').then((m) => m.Home),
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register').then((m) => m.Register),
  },
  {
    path: 'profile',
    loadComponent: () => import('./pages/profile/profile').then((m) => m.Profile),
    canActivate: [artisanGuard],
  },
  {
    path: 'profile/products',
    loadComponent: () => import('./pages/my-products/my-products').then((m) => m.MyProducts),
    canActivate: [artisanGuard],
  },
  {
    path: 'browse',
    loadComponent: () => import('./pages/browse/browse').then((m) => m.Browse),
  },
  {
    path: 'products/:id',
    loadComponent: () => import('./pages/product-detail/product-detail').then((m) => m.ProductDetail),
  },
  {
    path: '**',
    loadComponent: () => import('./pages/not-found/not-found').then((m) => m.NotFound),
  },
];
