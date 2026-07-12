import { CartActions } from './cart.actions';
import { cartFeature } from './cart.reducer';
import { CartItem, initialCartState } from './cart.state';

describe('cartFeature reducer', () => {
  const item: Omit<CartItem, 'quantity'> = {
    productId: 'product-1',
    productName: 'Zellige Tile Set',
    priceAmount: 450,
    priceCurrency: 'MAD',
    craftType: 'Pottery',
    imageUrl: null,
  };

  it('adds a new item', () => {
    const state = cartFeature.reducer(initialCartState, CartActions.addItem({ item, quantity: 2 }));

    expect(state.items).toEqual([{ ...item, quantity: 2 }]);
  });

  it('merges quantity when the same product is added again', () => {
    let state = cartFeature.reducer(initialCartState, CartActions.addItem({ item, quantity: 1 }));
    state = cartFeature.reducer(state, CartActions.addItem({ item, quantity: 3 }));

    expect(state.items).toEqual([{ ...item, quantity: 4 }]);
  });

  it('updates the quantity of an existing item', () => {
    let state = cartFeature.reducer(initialCartState, CartActions.addItem({ item, quantity: 1 }));
    state = cartFeature.reducer(state, CartActions.updateQuantity({ productId: item.productId, quantity: 5 }));

    expect(state.items[0].quantity).toBe(5);
  });

  it('removes the item when quantity is updated to zero', () => {
    let state = cartFeature.reducer(initialCartState, CartActions.addItem({ item, quantity: 1 }));
    state = cartFeature.reducer(state, CartActions.updateQuantity({ productId: item.productId, quantity: 0 }));

    expect(state.items).toEqual([]);
  });

  it('removes an item', () => {
    let state = cartFeature.reducer(initialCartState, CartActions.addItem({ item, quantity: 1 }));
    state = cartFeature.reducer(state, CartActions.removeItem({ productId: item.productId }));

    expect(state.items).toEqual([]);
  });

  it('clears the cart', () => {
    let state = cartFeature.reducer(initialCartState, CartActions.addItem({ item, quantity: 1 }));
    state = cartFeature.reducer(state, CartActions.clearCart());

    expect(state).toEqual(initialCartState);
  });
});
