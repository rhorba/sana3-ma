export type MembershipRole = 'OWNER' | 'MEMBER';
export type InviteStatus = 'PENDING' | 'ACCEPTED' | 'DECLINED';

export interface CooperativeMemberResponse {
  userId: string;
  email: string;
  role: MembershipRole;
  joinedAt: string;
}

export interface CooperativeInviteResponse {
  id: string;
  artisanProfileId: string;
  artisanDisplayName: string | null;
  status: InviteStatus;
  createdAt: string;
}

export interface InviteMemberRequest {
  email: string;
}
