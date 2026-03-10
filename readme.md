# Spring Boot CMS

A multi-tenant content management system built with Spring Boot, MySQL, and JWT authentication as an API.

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

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Login and receive a JWT token |
| `POST` | `/api/auth/register` | Register a new user |

### Posts (`/api/posts`)

Read endpoints are public. Write operations are restricted to **USER** and **SUPERADMIN** roles.

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/posts` | List posts (paginated, `tenantId` required for unauthenticated, optional for superadmin, ignored for regular users) |
| `GET` | `/api/posts/{postId}` | Get a single post (`tenantId` required for unauthenticated, optional for superadmin, ignored for regular users) |
| `POST` | `/api/posts` | Create a post |
| `PUT` | `/api/posts/{postId}` | Update a post |
| `DELETE` | `/api/posts/{postId}` | Delete a post |

### Events (`/api/events`)

Read endpoints are public. Write operations are restricted to **USER** and **SUPERADMIN** roles. The list endpoint only returns upcoming events (start date/time in the future).

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/events` | List upcoming events (paginated, `tenantId` required for unauthenticated, optional for superadmin, ignored for regular users) |
| `GET` | `/api/events/{eventId}` | Get a single event (`tenantId` required for unauthenticated, optional for superadmin, ignored for regular users) |
| `POST` | `/api/events` | Create an event |
| `PUT` | `/api/events/{eventId}` | Update an event |
| `DELETE` | `/api/events/{eventId}` | Delete an event |

### Tenants (`/api/admin/tenants`)

Restricted to **SUPERADMIN** only.

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/admin/tenants` | List tenants (paginated) |
| `GET` | `/api/admin/tenants/{tenantId}` | Get a single tenant |
| `POST` | `/api/admin/tenants` | Create a tenant |
| `PUT` | `/api/admin/tenants/{tenantId}` | Update a tenant |
| `DELETE` | `/api/admin/tenants/{tenantId}` | Delete a tenant |

### Users (`/api/admin/users`)

Restricted to **SUPERADMIN** only.

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/admin/users` | List users (paginated) |
| `GET` | `/api/admin/users/{userId}` | Get a single user |
| `POST` | `/api/admin/users` | Create a user |
| `PUT` | `/api/admin/users/{userId}` | Update a user |
| `DELETE` | `/api/admin/users/{userId}` | Delete a user |

## Roles

| Role | Permissions |
|---|---|
| **USER** | Login, read/create/update/delete posts and events within own tenant |
| **SUPERADMIN** | Full access to all tenants, manage tenants, manage users, manage posts and events across tenants |
