import { API_BASE_URL } from './api.config';

// Backend origin without the /api/v1 suffix, for resolving relative resource URLs the API
// returns (e.g. product image paths) into absolute <img src> URLs. Kept out of api.config.ts
// since that file is regenerated wholesale by the Docker build (see its comment).
export const API_ORIGIN = new URL(API_BASE_URL).origin;
