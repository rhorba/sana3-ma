import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { ArtisanOrderItemResponse, OrderResponse, PlaceOrderRequest } from './order.models';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/orders`;
  private readonly artisanBaseUrl = `${API_BASE_URL}/artisan-profiles/me/orders`;

  placeOrder(request: PlaceOrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.baseUrl, request);
  }

  listMyOrders(): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.baseUrl}/me`);
  }

  cancelOrder(id: string): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.baseUrl}/me/${id}/cancel`, {});
  }

  listArtisanOrderItems(): Observable<ArtisanOrderItemResponse[]> {
    return this.http.get<ArtisanOrderItemResponse[]>(this.artisanBaseUrl);
  }

  completeArtisanOrderItem(id: string): Observable<ArtisanOrderItemResponse> {
    return this.http.post<ArtisanOrderItemResponse>(`${this.artisanBaseUrl}/${id}/complete`, {});
  }
}
