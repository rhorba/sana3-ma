import { selectCartItemCount, selectCartTotalsByCurrency } from './cart.selectors';
import { CartItem } from './cart.state';

describe('cart selectors', () => {
  const items: CartItem[] = [
    {
      productId: 'p1',
      productName: 'Tile',
      priceAmount: 100,
      priceCurrency: 'MAD',
      craftType: 'Pottery',
      imageUrl: null,
      quantity: 2,
    },
    {
      productId: 'p2',
      productName: 'Rug',
      priceAmount: 20,
      priceCurrency: 'USD',
      craftType: 'Weaving',
      imageUrl: null,
      quantity: 1,
    },
    {
      productId: 'p3',
      productName: 'Bowl',
      priceAmount: 50,
      priceCurrency: 'MAD',
      craftType: 'Pottery',
      imageUrl: null,
      quantity: 1,
    },
  ];

  it('sums quantities across all items', () => {
    expect(selectCartItemCount.projector(items)).toBe(4);
  });

  it('returns 0 for an empty cart', () => {
    expect(selectCartItemCount.projector([])).toBe(0);
  });

  it('groups totals by currency', () => {
    const totals = selectCartTotalsByCurrency.projector(items);

    expect(totals).toEqual([
      { currency: 'MAD', amount: 250 },
      { currency: 'USD', amount: 20 },
    ]);
  });
});
