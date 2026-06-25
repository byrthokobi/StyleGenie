---
unit: 001-auth-service
bolt: 001-auth-service
stage: design
status: complete
updated: 2026-06-25T00:00:00Z
---

# Technical Design — Auth Service

## Architecture Pattern

**Layered Architecture** (Presentation → Application → Domain → Infrastructure) within a single Spring Boot module. Chosen for simplicity and team familiarity with Spring Boot conventions. Domain layer is kept pure (no Spring dependencies) to keep business rules testable without the framework.

---

## Layer Structure

```text
┌──────────────────────────────────────────────┐
│  Presentation Layer                          │
│  AuthController                              │  POST /auth/register, /login, /logout
├──────────────────────────────────────────────┤
│  Application Layer                           │
│  AuthenticationService                       │  Orchestrates register/login/logout flows
│  PasswordService, TokenService               │  Domain services (stateless)
│  DTOs: RegisterRequest, LoginRequest,        │
│        LoginResponse, LogoutRequest          │
├──────────────────────────────────────────────┤
│  Domain Layer                                │
│  User, RefreshToken (entities)               │  Pure Java — no Spring annotations
│  Email, RawPassword, HashedPassword,         │
│  Role, TokenPair (value objects)             │
│  UserRepository, RefreshTokenRepository      │  Interfaces only
├──────────────────────────────────────────────┤
│  Infrastructure Layer                        │
│  JpaUserRepository (Spring Data JPA)         │  Implements UserRepository
│  JpaRefreshTokenRepository                  │  Implements RefreshTokenRepository
│  JwtUtil                                    │  JWT generation + validation (jjwt)
│  JwtAuthenticationFilter                    │  Spring Security OncePerRequestFilter
│  SecurityConfig                             │  Spring Security bean configuration
└──────────────────────────────────────────────┘
```

---

## Package Structure

```text
com.stylegenie.auth/
├── domain/
│   ├── entity/
│   │   ├── User.java
│   │   └── RefreshToken.java
│   ├── vo/
│   │   ├── Email.java
│   │   ├── RawPassword.java
│   │   ├── HashedPassword.java
│   │   └── Role.java  (enum: REGULAR_USER, STYLIST, ADMIN)
│   ├── event/
│   │   ├── UserRegisteredEvent.java
│   │   ├── UserLoggedInEvent.java
│   │   └── UserLoggedOutEvent.java
│   └── repository/
│       ├── UserRepository.java
│       └── RefreshTokenRepository.java
├── application/
│   ├── service/
│   │   ├── AuthenticationService.java
│   │   ├── PasswordService.java
│   │   └── TokenService.java
│   └── dto/
│       ├── RegisterRequest.java
│       ├── LoginRequest.java
│       ├── LoginResponse.java
│       └── LogoutRequest.java
├── infrastructure/
│   ├── persistence/
│   │   ├── JpaUserRepository.java
│   │   └── JpaRefreshTokenRepository.java
│   └── security/
│       ├── JwtUtil.java
│       ├── JwtAuthenticationFilter.java
│       └── SecurityConfig.java
└── presentation/
    └── controller/
        └── AuthController.java
```

---

## API Design

### POST /auth/register

**Request**:
```json
{
  "email": "user@example.com",
  "password": "securePass123"
}
```

**Response 201 Created**:
```json
{
  "publicId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "role": "REGULAR_USER"
}
```

**Errors**: 400 (validation), 409 (email taken)

---

### POST /auth/login

**Request**:
```json
{
  "email": "user@example.com",
  "password": "securePass123"
}
```

**Response 200 OK**:
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "550e8400-e29b-41d4-a716-...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Errors**: 401 (invalid credentials or deactivated)

---

### POST /auth/logout

**Request**:
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-..."
}
```

**Response 200 OK**:
```json
{
  "message": "Logged out successfully"
}
```

**Errors**: 400 (missing token)

---

## Data Persistence

### Table: `users`

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGSERIAL | PRIMARY KEY |
| public_id | UUID | UNIQUE, NOT NULL, DEFAULT gen_random_uuid() |
| email | VARCHAR(255) | UNIQUE, NOT NULL |
| password_hash | VARCHAR(255) | NOT NULL |
| role | VARCHAR(50) | NOT NULL, DEFAULT 'REGULAR_USER' |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL |

> `id` is the internal PK used for all FK joins and indexing. `public_id` is the UUID exposed in API responses — never expose `id` externally.

### Table: `refresh_tokens`

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGSERIAL | PRIMARY KEY |
| user_id | BIGINT | NOT NULL, FK → users(id) |
| token | VARCHAR(255) | UNIQUE, NOT NULL |
| expires_at | TIMESTAMP WITH TIME ZONE | NOT NULL |
| revoked | BOOLEAN | NOT NULL, DEFAULT FALSE |
| created_at | TIMESTAMP WITH TIME ZONE | NOT NULL |

**Indexes**:
- `refresh_tokens(token)` — fast lookup on logout and refresh
- `refresh_tokens(user_id)` — fast revocation by user on account deactivation

### Migration Strategy

Flyway migrations in `src/main/resources/db/migration/`:
- `V1__create_users_table.sql`
- `V2__create_refresh_tokens_table.sql`

---

## Security Design

| Concern | Approach |
|---------|----------|
| Password hashing | BCrypt via Spring Security `PasswordEncoder` (cost factor 10, ~100ms per hash) |
| Access token | JWT signed with HS256; secret loaded from env var `JWT_SECRET` (min 256-bit); 15-minute expiry; payload: `{ sub: userId, role: ROLE, iat, exp }` |
| Refresh token | Opaque UUID stored in DB; 7-day expiry; checked for revocation on every use |
| Token transport | Authorization header: `Bearer <accessToken>`; refresh token in request body |
| Public endpoints | `/auth/register`, `/auth/login`, `/auth/logout` — no JWT required |
| Protected endpoints | All other routes require valid JWT in Authorization header |
| Spring Security config | `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`; sets `SecurityContextHolder` on valid JWT |
| Role enforcement | `@PreAuthorize("hasRole('ADMIN')")` on admin endpoints (wired in bolt 002-auth-service) |
| Deactivated users | Login check: if `user.isActive == false` → 401 before token issuance |

---

## NFR Implementation

| Requirement | Design Approach |
|-------------|-----------------|
| Login < 500ms p95 | BCrypt cost factor 10 (~100ms); HikariCP connection pool (Spring Boot default, 10 connections); indexed email column for fast user lookup |
| No plain-text passwords | `RawPassword` VO is only used at the boundary; `PasswordService.hash()` is the only path to `HashedPassword`; raw value never logged |
| Token statelessness (access) | JWT is self-contained — no DB lookup on access token validation (only signature + expiry check) |
| Refresh token security | DB-backed revocation check on every refresh use; `deleteAllExpired()` called on a scheduled basis (simple `@Scheduled` for MVP) |

---

## Error Handling

| Scenario | HTTP Code | Response Body |
|----------|-----------|---------------|
| Blank / invalid email format | 400 | `{ "error": "Validation failed", "details": ["email: must be a valid email"] }` |
| Password shorter than 8 chars | 400 | `{ "error": "Validation failed", "details": ["password: must be at least 8 characters"] }` |
| Email already registered | 409 | `{ "error": "Email already in use" }` |
| Wrong credentials / email not found | 401 | `{ "error": "Invalid credentials" }` (same message for both — no email enumeration) |
| Deactivated account login | 401 | `{ "error": "Account is deactivated" }` |
| Missing / expired JWT | 401 | `{ "error": "Unauthorised" }` |
| Revoked / expired refresh token | 401 | `{ "error": "Invalid or expired refresh token" }` |
| Missing refresh token on logout | 400 | `{ "error": "Refresh token is required" }` |
| Unexpected server error | 500 | `{ "error": "An unexpected error occurred" }` (no stack trace) |

All errors handled via a global `@RestControllerAdvice` exception handler.

---

## External Dependencies

| Library | Purpose | Integration |
|---------|---------|-------------|
| `spring-boot-starter-security` | Spring Security filter chain, `PasswordEncoder` bean | Spring Boot autoconfiguration |
| `io.jsonwebtoken:jjwt-api` + `jjwt-impl` + `jjwt-jackson` | JWT generation and validation | `JwtUtil` service wrapper |
| `spring-boot-starter-data-jpa` | JPA repositories, entity management | Spring Data JPA |
| `postgresql` JDBC driver | PostgreSQL connectivity | Spring Boot datasource config |
| `flyway-core` | Database migrations | Spring Boot Flyway autoconfiguration |
| `spring-boot-starter-validation` | Bean Validation (`@Valid`, `@Email`, `@Size`) | Controller request validation |
