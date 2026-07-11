export interface UpsertProductRequest {
  name: string;
  description: string;
  priceAmount: number;
  priceCurrency: string;
  craftType: string;
}

export interface ProductResponse {
  id: string;
  artisanProfileId: string;
  name: string;
  description: string | null;
  priceAmount: number;
  priceCurrency: string;
  craftType: string;
  imageUrl: string | null;
  createdAt: string;
  updatedAt: string;
}
