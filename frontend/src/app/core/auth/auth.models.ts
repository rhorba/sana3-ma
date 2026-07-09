import { Role } from '../../store/auth/auth.state';

export type RegistrableRole = Extract<Role, 'BUYER' | 'ARTISAN'>;

export interface RegisterRequest {
  email: string;
  password: string;
  role: RegistrableRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  userId: string;
  email: string;
  role: Role;
  accessToken: string;
  expiresInSeconds: number;
}
