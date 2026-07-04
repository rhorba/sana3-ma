# Sana3.ma — Moroccan Artisan Marketplace

Morocco's zellige, leather, carpets and woodwork are world-famous but sold through informal channels with zero authenticity guarantee.

## Problem
Artisans lack online reach. Buyers (domestic and export) can't verify authenticity. No certification tracking.

## Solution
Marketplace + QR-authenticated craft certificates (reusing Terroir.ma's QR system), cooperative accounts, export-ready DHL integration.

## Stack
Java Spring Boot (latest, hexagonal + CQRS-lite), Angular (latest, NgRx), PostgreSQL 16 + PostGIS (regional origin), Docker Compose (Kubernetes if scale demands it), QR generation, CMI + Stripe (export)

See `docs/` for the full foundation docs (PRD, architecture, security, database, UX/UI, test strategy, DevOps, stories).

## Completes
Terroir.ma (agri certification → artisan certification)

## Key Roles
Artisan / Cooperative | Buyer | Auditor | Admin
