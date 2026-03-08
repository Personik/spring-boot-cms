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

All API calls (except login) require a JWT token in the `Authorization` header.
Check the AuthController for the login API call.

Use the token in subsequent requests:

```
Authorization: Bearer eyJhbG...
```

## API Endpoints

### Posts (`/api/posts`)

Available to all authenticated users. Write operations are restricted to **USER** and **SUPERADMIN** roles.

- **USER** users only see posts belonging to their own tenant.
- **SUPERADMIN** users see posts across all tenants.
- When a **SUPERADMIN** creates a post, they must include `"tenantId"` in the request body.

### Tenants (`/api/admin/tenants`)

Restricted to **SUPERADMIN** only.


### Users (`/api/admin/users`)

Restricted to **SUPERADMIN** only.

`tenantId` is required for `USER` role and ignored for `SUPERADMIN`.

## Roles

| Role | Permissions |
|---|---|
| **USER** | Login, read/create/update/delete posts within own tenant |
| **SUPERADMIN** | Full access to all tenants, manage tenants, manage users, manage posts across tenants |
