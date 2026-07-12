export interface CartItem {
  productId: string;
  productName: string;
  priceAmount: number;
  priceCurrency: string;
  craftType: string;
  imageUrl: string | null;
  quantity: number;
}

export interface CartState {
  items: CartItem[];
}

export const initialCartState: CartState = {
  items: [],
};
