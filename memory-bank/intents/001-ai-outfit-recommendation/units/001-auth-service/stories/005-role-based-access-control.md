---
id: 005-role-based-access-control
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 002-auth-service
implemented: false
---

# Story: 005-role-based-access-control

## User Story

**As a** platform operator
**I want** all API endpoints to enforce role-based access control
**So that** regular users cannot access admin functionality and unauthenticated users cannot access protected resources

## Acceptance Criteria

- [ ] **Given** I am unauthenticated, **When** I call any protected endpoint, **Then** I receive a 401 Unauthorized
- [ ] **Given** I am authenticated as REGULAR_USER, **When** I call an ADMIN-only endpoint, **Then** I receive a 403 Forbidden
- [ ] **Given** I am authenticated as ADMIN, **When** I call any endpoint (user or admin), **Then** my request proceeds normally
- [ ] **Given** a user registers via POST /auth/register, **When** the account is created, **Then** the role is always set to REGULAR_USER (not self-assignable to ADMIN)
- [ ] **Given** a JWT contains an invalid or tampered signature, **When** used on any protected endpoint, **Then** the request is rejected with 401

## Technical Notes

- Roles: REGULAR_USER, ADMIN (stored as enum in User entity)
- Enforced via Spring Security `@PreAuthorize` annotations and filter chain
- JWT payload includes `role` claim; verified on every protected request
- Public endpoints (no auth required): POST /auth/register, POST /auth/login, POST /auth/refresh

## Dependencies

### Requires
- 001-user-registration (role set at registration)
- 002-user-login (role embedded in JWT)

### Enables
- 005-admin-service (ADMIN role enforcement)
- All other protected endpoints

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| JWT with expired claim | 401 Unauthorized |
| JWT with ADMIN role for deactivated user | 401 Unauthorized |
| Missing Authorization header | 401 Unauthorized |

## Out of Scope

- Fine-grained permissions beyond two roles — future enhancement
- Permission management UI — future enhancement
