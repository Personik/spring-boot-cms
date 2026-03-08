# Spring Boot CMS

A multi-tenant content management system built with Spring Boot, MySQL, and JWT authentication.

## Prerequisites

- Docker & Docker Compose

## Setup

### 1. Configure environment variables

Copy the example file and adjust values as needed:

```bash
cp .env_example .env
```

Open `.env` and review the settings.

**Important:** Change `CMS_JWT_SECRET` to a strong, random value before deploying.

### 2. Start the application

```bash
docker compose up
```

The app will be available at `http://localhost:6868` (or whichever port you set in `SPRING_LOCAL_PORT`).

### 3. Database auto-setup

Hibernate automatically creates/updates the database tables on startup (`spring.jpa.hibernate.ddl-auto=update`). No manual SQL scripts are needed.

## Data Seeder

On the first startup (when the `users` table is empty), the application automatically seeds some sample data.
The seeder runs only once. On subsequent restarts, existing data is left untouched.

## Authentication

All API calls (except login and register) require a JWT token in the `Authorization` header.
Check the AuthController for the login and register API calls.

Use the token in subsequent requests:

```
Authorization: Bearer eyJhbG...
```

## API Endpoints

### Auth (`/api/auth`)

No authentication required.
Check AuthController for Endpoints.

### Posts (`/api/posts`)

Write operations are restricted to **USER** and **SUPERADMIN** roles.
Check PostController for Endpoints .

### Events (`/api/events`)

Same security rules as posts. The list endpoint only returns upcoming events (start date/time in the future).
Check EventController for Endpoints.

### Tenants (`/api/admin/tenants`)

Restricted to **SUPERADMIN** only.
Check TenantController for Endpoints.

### Users (`/api/admin/users`)

Restricted to **SUPERADMIN** only.
Check UserController for Endpoints.

## Roles

| Role | Permissions |
|---|---|
| **USER** | Login, read/create/update/delete posts and events within own tenant |
| **SUPERADMIN** | Full access to all tenants, manage tenants, manage users, manage posts and events across tenants |
