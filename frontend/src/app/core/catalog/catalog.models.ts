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

export interface PublicArtisanSummary {
  displayName: string;
  craftType: string;
  region: string | null;
}

export interface PublicProductResponse {
  id: string;
  name: string;
  description: string | null;
  priceAmount: number;
  priceCurrency: string;
  craftType: string;
  imageUrl: string | null;
  artisan: PublicArtisanSummary;
}

export interface PublicProductPageResponse {
  products: PublicProductResponse[];
  totalElements: number;
  page: number;
  pageSize: number;
}

export interface ProductSearchFilters {
  craftType?: string;
  region?: string;
  minPrice?: number;
  maxPrice?: number;
  q?: string;
  page?: number;
  pageSize?: number;
}
