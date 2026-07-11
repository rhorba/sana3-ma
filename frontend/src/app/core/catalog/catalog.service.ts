import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import {
  ProductResponse,
  ProductSearchFilters,
  PublicProductPageResponse,
  PublicProductResponse,
  UpsertProductRequest,
} from './catalog.models';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/artisan-profiles/me/products`;
  private readonly publicBaseUrl = `${API_BASE_URL}/products`;

  listMyProducts(): Observable<ProductResponse[]> {
    return this.http.get<ProductResponse[]>(this.baseUrl);
  }

  createProduct(request: UpsertProductRequest): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(this.baseUrl, request);
  }

  updateProduct(id: string, request: UpsertProductRequest): Observable<ProductResponse> {
    return this.http.put<ProductResponse>(`${this.baseUrl}/${id}`, request);
  }

  deleteProduct(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  uploadImage(id: string, file: File): Observable<ProductResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ProductResponse>(`${this.baseUrl}/${id}/image`, formData);
  }

  searchProducts(filters: ProductSearchFilters): Observable<PublicProductPageResponse> {
    let params = new HttpParams();
    if (filters.craftType) params = params.set('craftType', filters.craftType);
    if (filters.region) params = params.set('region', filters.region);
    if (filters.minPrice != null) params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice != null) params = params.set('maxPrice', filters.maxPrice);
    if (filters.q) params = params.set('q', filters.q);
    if (filters.page != null) params = params.set('page', filters.page);
    if (filters.pageSize != null) params = params.set('pageSize', filters.pageSize);
    return this.http.get<PublicProductPageResponse>(this.publicBaseUrl, { params });
  }

  getProductDetail(id: string): Observable<PublicProductResponse> {
    return this.http.get<PublicProductResponse>(`${this.publicBaseUrl}/${id}`);
  }
}
