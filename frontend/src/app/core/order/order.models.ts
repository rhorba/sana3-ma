export interface PlaceOrderLineItemRequest {
  productId: string;
  quantity: number;
}

export interface PlaceOrderRequest {
  shippingAddress: string;
  items: PlaceOrderLineItemRequest[];
}

export interface OrderItemResponse {
  id: string;
  productId: string | null;
  productName: string;
  priceAmount: number;
  priceCurrency: string;
  craftType: string;
  artisanProfileId: string;
  quantity: number;
  lineTotal: number;
  completed: boolean;
  completedAt: string | null;
}

export interface OrderTotalResponse {
  currency: string;
  amount: number;
}

export type OrderStatus = 'PLACED' | 'COMPLETED' | 'CANCELLED';

export interface OrderResponse {
  id: string;
  buyerUserId: string;
  status: OrderStatus;
  shippingAddress: string;
  items: OrderItemResponse[];
  totals: OrderTotalResponse[];
  createdAt: string;
  updatedAt: string;
}
