import { createSelector } from '@ngrx/store';

import { cartFeature } from './cart.reducer';

export const { selectItems: selectCartItems } = cartFeature;

export const selectCartItemCount = createSelector(selectCartItems, (items) =>
  items.reduce((sum, item) => sum + item.quantity, 0),
);

// Mirrors the backend's per-currency order totals (docs/database-sana3-ma.md, Sprint 3 multi-currency
// note) — no assumption that every item shares one currency.
export const selectCartTotalsByCurrency = createSelector(selectCartItems, (items) => {
  const totals = new Map<string, number>();
  for (const item of items) {
    totals.set(item.priceCurrency, (totals.get(item.priceCurrency) ?? 0) + item.priceAmount * item.quantity);
  }
  return Array.from(totals.entries())
    .map(([currency, amount]) => ({ currency, amount }))
    .sort((a, b) => a.currency.localeCompare(b.currency));
});
