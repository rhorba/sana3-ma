FROM node:22-alpine AS build
WORKDIR /app
ARG API_BASE_URL=http://localhost:8081/api/v1
COPY package*.json .npmrc ./
RUN npm ci
COPY . .
RUN sh -c "echo \"export const API_BASE_URL = '${API_BASE_URL}';\" > src/app/core/api.config.ts"
RUN npm run build

FROM nginx:alpine
RUN apk upgrade --no-cache
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
