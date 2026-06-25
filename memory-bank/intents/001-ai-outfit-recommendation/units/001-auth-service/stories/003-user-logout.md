---
id: 003-user-logout
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 001-auth-service
implemented: false
---

# Story: 003-user-logout

## User Story

**As a** logged-in user
**I want** to log out of StyleGenie
**So that** my session is ended and my refresh token can no longer be used

## Acceptance Criteria

- [ ] **Given** I have a valid refresh token, **When** I call POST /auth/logout with that token, **Then** I receive a 200 OK and the refresh token is marked as revoked in the database
- [ ] **Given** I attempt to use a revoked refresh token, **When** I call POST /auth/refresh, **Then** I receive a 401 Unauthorized
- [ ] **Given** I call logout without a token, **When** the request is processed, **Then** I receive a 400 Bad Request

## Technical Notes

- Logout revokes the refresh token in the DB (sets `revoked = true`)
- Access tokens are stateless JWTs — they expire naturally after 15 min; no server-side revocation needed for MVP
- Frontend should discard the access token from memory on logout

## Dependencies

### Requires
- 002-user-login (must be logged in to log out)

### Enables
- None

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Logout called twice with same token | Second call returns 200 (idempotent) |
| Logout with an already-expired refresh token | 200 returned (token is effectively already invalid) |

## Out of Scope

- Logging out all sessions (invalidate all refresh tokens) — future enhancement
