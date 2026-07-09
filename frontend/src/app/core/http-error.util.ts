import { HttpErrorResponse } from '@angular/common/http';

import { ApiError } from './api-error.model';

export function extractErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as ApiError | undefined;
    return body?.error?.message ?? fallback;
  }
  return fallback;
}
