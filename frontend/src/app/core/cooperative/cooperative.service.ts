import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../api.config';
import {
  CooperativeInviteResponse,
  CooperativeMemberResponse,
  InviteMemberRequest,
} from './cooperative.models';

@Injectable({ providedIn: 'root' })
export class CooperativeService {
  private readonly http = inject(HttpClient);

  getMembers(): Observable<CooperativeMemberResponse[]> {
    return this.http.get<CooperativeMemberResponse[]>(`${API_BASE_URL}/artisan-profiles/me/members`);
  }

  inviteMember(request: InviteMemberRequest): Observable<CooperativeInviteResponse> {
    return this.http.post<CooperativeInviteResponse>(
      `${API_BASE_URL}/artisan-profiles/me/members/invites`,
      request,
    );
  }

  removeMember(targetUserId: string): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/artisan-profiles/me/members/${targetUserId}`);
  }

  getMyInvites(): Observable<CooperativeInviteResponse[]> {
    return this.http.get<CooperativeInviteResponse[]>(`${API_BASE_URL}/cooperative-invites/me`);
  }

  acceptInvite(inviteId: string): Observable<CooperativeInviteResponse> {
    return this.http.post<CooperativeInviteResponse>(
      `${API_BASE_URL}/cooperative-invites/me/${inviteId}/accept`,
      {},
    );
  }

  declineInvite(inviteId: string): Observable<CooperativeInviteResponse> {
    return this.http.post<CooperativeInviteResponse>(
      `${API_BASE_URL}/cooperative-invites/me/${inviteId}/decline`,
      {},
    );
  }
}
