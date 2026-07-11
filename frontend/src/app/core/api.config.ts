// Dev-only default for `ng serve`. The Docker build overwrites this file *entirely* at build
// time via the API_BASE_URL build ARG in frontend/Dockerfile (a single generated `export const`
// line) -- do not add other exports here, they will not survive the Docker build. Anything else
// api.config-related (e.g. deriving the backend origin) belongs in a separate file that imports
// API_BASE_URL instead.
export const API_BASE_URL = 'http://localhost:8081/api/v1';
