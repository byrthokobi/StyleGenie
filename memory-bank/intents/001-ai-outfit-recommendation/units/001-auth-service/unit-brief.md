---
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
phase: inception
status: draft
created: 2026-06-25T00:00:00Z
updated: 2026-06-25T00:00:00Z
---

# Unit Brief: auth-service

## Purpose

Manages user identity for StyleGenie — registration, login, logout, JWT token lifecycle, and role-based access control. Every other unit depends on this unit to authenticate requests.

## Scope

### In Scope
- User registration (email + password)
- User login and JWT access/refresh token issuance
- User logout (token invalidation)
- JWT token refresh
- Role assignment and enforcement (REGULAR_USER, ADMIN)
- Password hashing and validation

### Out of Scope
- Social login (OAuth with Google, Facebook) — future enhancement
- MFA — future enhancement
- Profile data (body shape, skin tone) — handled by `002-profile-service`

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-1 | Users can register with email/password, log in, receive JWT, log out, and have passwords hashed | Must |

---

## Domain Concepts

### Key Entities

| Entity | Description | Attributes |
|--------|-------------|------------|
| User | Core identity record | id, email, passwordHash, role, createdAt, isActive |
| RefreshToken | Issued on login, used to obtain new access tokens | id, userId, token, expiresAt, revoked |

### Key Operations

| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| register | Create new user account | email, password | User record |
| login | Authenticate user, issue tokens | email, password | accessToken, refreshToken |
| logout | Revoke refresh token | refreshToken | Confirmation |
| refreshTokens | Issue new access token | refreshToken | new accessToken |
| validateToken | Validate JWT for protected endpoints | JWT | UserPrincipal or 401 |

---

## Story Summary

| Metric | Count |
|--------|-------|
| Total Stories | 5 |
| Must Have | 5 |
| Should Have | 0 |
| Could Have | 0 |

### Stories

| Story ID | Title | Priority | Status |
|----------|-------|----------|--------|
| 001-user-registration | User can register with email and password | Must | Planned |
| 002-user-login | User can log in and receive JWT tokens | Must | Planned |
| 003-user-logout | User can log out and invalidate session | Must | Planned |
| 004-jwt-token-refresh | User can refresh access token using refresh token | Must | Planned |
| 005-role-based-access-control | Endpoints enforce REGULAR_USER and ADMIN roles | Must | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| None | First unit — no upstream dependencies |

### Depended By
| Unit | Reason |
|------|--------|
| 002-profile-service | Needs authenticated user identity |
| 003-wardrobe-service | Needs authenticated user identity |
| 004-recommendation-service | Needs authenticated user identity |
| 005-admin-service | Needs ADMIN role enforcement |
| 006-ai-outfit-recommendation-ui | Consumes auth API for login/register screens |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| PostgreSQL | Persist User and RefreshToken records | Low |

---

## Technical Context

### Suggested Technology
- Spring Boot + Spring Security + JWT (jjwt library)
- BCrypt for password hashing
- PostgreSQL for persistence via Spring Data JPA

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| All other backend units | JWT validation filter | Spring Security filter chain |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| Users | PostgreSQL | Low-medium | Permanent |
| Refresh tokens | PostgreSQL | Low-medium | Until revoked/expired |

---

## Constraints

- Passwords must never be stored in plain text — BCrypt only
- Access tokens: short-lived (15 min); Refresh tokens: longer-lived (7 days)
- ADMIN role cannot be self-assigned during registration

---

## Success Criteria

### Functional
- [ ] User can register with unique email and receive 201 Created
- [ ] User can log in with correct credentials and receive access + refresh tokens
- [ ] User can log out and refresh token is invalidated
- [ ] User can refresh access token using a valid refresh token
- [ ] Protected endpoints return 401 for unauthenticated requests and 403 for wrong role

### Non-Functional
- [ ] Login endpoint responds within 500ms p95
- [ ] Passwords hashed with BCrypt (cost factor ≥ 10)

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 001-auth-service | ddd-construction-bolt | 001, 002, 003 | Core auth: register, login, logout |
| 002-auth-service | ddd-construction-bolt | 004, 005 | Token refresh + RBAC enforcement |
