---
unit: 001-auth-service
bolt: 001-auth-service
stage: model
status: complete
updated: 2026-06-25T00:00:00Z
---

# Static Model — Auth Service

## Bounded Context

**Authentication & Identity** — responsible for all user identity concerns within StyleGenie: account creation, credential verification, session lifecycle via JWT tokens, and role assignment. No other bounded context manages passwords, tokens, or roles. All other contexts consume identity by validating a JWT; they do not reach into this context's data store.

---

## Domain Entities

| Entity | Properties | Business Rules |
|--------|------------|----------------|
| **User** | id (Long — auto-increment PK), publicId (UUID — external identifier), email (Email VO), passwordHash (HashedPassword VO), role (Role), isActive (boolean), createdAt (Instant) | Email must be unique across all users; email stored lowercase; password never stored in plain text; role defaults to REGULAR_USER on registration; ADMIN and STYLIST roles cannot be self-assigned — only an ADMIN can assign them; publicId is exposed in API responses, id is internal only |
| **RefreshToken** | id (Long — auto-increment PK), userId (Long — FK to User.id), token (String — opaque UUID), expiresAt (Instant), revoked (boolean), createdAt (Instant) | One refresh token issued per login; token must be revoked on logout; expired or revoked tokens are rejected on use; userId must reference an existing, active User |

---

## Value Objects

| Value Object | Properties | Constraints |
|--------------|------------|-------------|
| **Email** | value (String) | Must match RFC 5322 format; stored and compared in lowercase; immutable after creation |
| **RawPassword** | value (String) | Minimum 8 characters; must not be blank or whitespace-only; used only at the registration/login boundary — never persisted |
| **HashedPassword** | value (String) | BCrypt hash (cost factor ≥ 10); immutable; only created via `PasswordService.hash(RawPassword)` |
| **Role** | REGULAR_USER, STYLIST, ADMIN | Enum; default is REGULAR_USER; cannot be set to ADMIN or STYLIST by the registering user (admin-assigned only) |
| **TokenPair** | accessToken (String — JWT), refreshToken (String — opaque) | Produced together on login; access token carries userId + role claims; refresh token is opaque |

---

## Aggregates

| Aggregate Root | Members | Invariants |
|----------------|---------|------------|
| **User** | Email VO, HashedPassword VO, Role, isActive | A User is the root. RefreshTokens are owned by User but stored separately (not inside the User aggregate) to allow independent expiry management. User email uniqueness is an aggregate invariant enforced before `save`. A deactivated User (isActive = false) may not log in. |
| **RefreshToken** | (standalone, linked by userId) | Not a member of the User aggregate — managed independently. Token is immutable after creation; only `revoked` flag changes. |

---

## Domain Events

| Event | Trigger | Payload |
|-------|---------|---------|
| **UserRegistered** | User account successfully created | userId, email, role, createdAt |
| **UserLoggedIn** | User authenticated successfully | userId, role, issuedAt |
| **UserLoggedOut** | Refresh token revoked on logout | userId, tokenId, revokedAt |

> Note: For MVP, events are informational (used for activity logging in admin-service). No async event bus required — events can be logged synchronously as ActivityLog entries.

---

## Domain Services

| Service | Operations | Dependencies |
|---------|------------|--------------|
| **PasswordService** | `hash(RawPassword) → HashedPassword`, `verify(RawPassword, HashedPassword) → boolean` | BCrypt library |
| **TokenService** | `generateAccessToken(User) → String (JWT)`, `generateRefreshToken(User) → RefreshToken`, `validateAccessToken(String) → UserPrincipal`, `revokeRefreshToken(token: String)` | JWT library (jjwt), RefreshTokenRepository |
| **AuthenticationService** | `register(email, RawPassword) → User`, `login(email, RawPassword) → TokenPair`, `logout(refreshToken: String)` | UserRepository, PasswordService, TokenService |

---

## Repository Interfaces

| Repository | Entity | Methods |
|------------|--------|---------|
| **UserRepository** | User | `save(User) → User`, `findByEmail(Email) → Optional<User>`, `findById(UUID) → Optional<User>` |
| **RefreshTokenRepository** | RefreshToken | `save(RefreshToken) → RefreshToken`, `findByToken(String) → Optional<RefreshToken>`, `revokeByToken(String)`, `deleteAllExpired()` |

---

## Ubiquitous Language

| Term | Definition |
|------|------------|
| **Registration** | The act of creating a new StyleGenie user account by providing a unique email and a valid password. Results in a User record with a hashed password and REGULAR_USER role. |
| **Authentication** | Verifying a user's identity by checking their email and password against stored credentials. Produces a TokenPair on success. |
| **Access Token** | A short-lived JWT (15 minutes) containing the user's ID and role, used to authorise every protected API request. |
| **Refresh Token** | A long-lived opaque token (7 days) stored in the database, used exclusively to obtain a new access token without re-entering credentials. |
| **Session** | The period of authorised access between a successful login and either logout or refresh token expiry. Represented by a non-revoked, non-expired RefreshToken. |
| **Deactivation** | An admin action that sets a User's `isActive` to false, preventing all future logins. Existing refresh tokens are also revoked. |
| **Role** | A permission level assigned to a user. `REGULAR_USER` may access standard features; `STYLIST` is reserved for stylist accounts (future use); `ADMIN` may access administrative endpoints. Roles are enforced by Spring Security at the endpoint level. Only ADMIN can assign the STYLIST or ADMIN role. |
| **Hashed Password** | The BCrypt representation of a user's raw password. The raw password is discarded immediately after hashing; only the hash is stored. |

---

## Story Coverage Check

| Story | Covered By |
|-------|-----------|
| 001-user-registration | User entity, Email VO, RawPassword VO, HashedPassword VO, PasswordService, UserRepository, UserRegistered event |
| 002-user-login | AuthenticationService.login, TokenService, TokenPair VO, RefreshToken entity, RefreshTokenRepository, UserLoggedIn event |
| 003-user-logout | AuthenticationService.logout, TokenService.revokeRefreshToken, RefreshTokenRepository.revokeByToken, UserLoggedOut event |
