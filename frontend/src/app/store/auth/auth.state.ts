export type Role = 'ARTISAN' | 'BUYER' | 'ADMIN';

export interface AuthState {
  userId: string | null;
  email: string | null;
  role: Role | null;
  accessToken: string | null;
  loading: boolean;
  error: string | null;
}

export const initialAuthState: AuthState = {
  userId: null,
  email: null,
  role: null,
  accessToken: null,
  loading: false,
  error: null,
};
