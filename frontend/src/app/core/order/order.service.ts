import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import { OrderResponse, PlaceOrderRequest } from './order.models';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/orders`;

  placeOrder(request: PlaceOrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.baseUrl, request);
  }
}
