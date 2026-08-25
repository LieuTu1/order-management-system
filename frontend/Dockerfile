# ===== Stage 1: Install deps =====
FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci

# ===== Stage 2: Build =====
FROM node:20-alpine AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

# URL backend được truyền vào lúc build (Next.js inline biến NEXT_PUBLIC_* lúc build)
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

RUN npm run build

# ===== Stage 3: Runtime =====
FROM node:20-alpine AS runtime
WORKDIR /app

ENV NODE_ENV=production

# Cần next.config.js có output: 'standalone' để có đủ 3 thứ dưới đây
COPY --from=build /app/public ./public
COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static

EXPOSE 3000

CMD ["node", "server.js"]