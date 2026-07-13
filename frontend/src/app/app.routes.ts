import { Routes } from '@angular/router';

import { artisanGuard, authGuard } from './core/auth/auth.guard';

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
    path: 'profile/orders',
    loadComponent: () => import('./pages/artisan-orders/artisan-orders').then((m) => m.ArtisanOrders),
    canActivate: [artisanGuard],
  },
  {
    path: 'profile/members',
    loadComponent: () =>
      import('./pages/cooperative-members/cooperative-members').then((m) => m.CooperativeMembers),
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
    path: 'cart',
    loadComponent: () => import('./pages/cart/cart').then((m) => m.Cart),
  },
  {
    path: 'checkout',
    loadComponent: () => import('./pages/checkout/checkout').then((m) => m.Checkout),
    canActivate: [authGuard],
  },
  {
    path: 'orders',
    loadComponent: () => import('./pages/orders/orders').then((m) => m.Orders),
    canActivate: [authGuard],
  },
  {
    path: '**',
    loadComponent: () => import('./pages/not-found/not-found').then((m) => m.NotFound),
  },
];
