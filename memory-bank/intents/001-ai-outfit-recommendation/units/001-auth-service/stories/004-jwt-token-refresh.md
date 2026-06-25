---
id: 004-jwt-token-refresh
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 002-auth-service
implemented: false
---

# Story: 004-jwt-token-refresh

## User Story

**As a** logged-in user whose access token has expired
**I want** to obtain a new access token using my refresh token
**So that** I can continue using StyleGenie without logging in again

## Acceptance Criteria

- [ ] **Given** I have a valid, non-expired, non-revoked refresh token, **When** I call POST /auth/refresh, **Then** I receive a 200 response with a new accessToken
- [ ] **Given** I have an expired refresh token, **When** I call POST /auth/refresh, **Then** I receive a 401 Unauthorized
- [ ] **Given** I have a revoked refresh token, **When** I call POST /auth/refresh, **Then** I receive a 401 Unauthorized
- [ ] **Given** a successful refresh, **When** the new access token is used on a protected endpoint, **Then** the request is authorised

## Technical Notes

- Refresh does not rotate the refresh token for MVP (rotation is a security enhancement for later)
- New access token contains same userId and role as original
- Refresh token validity checked against DB (not just expiry date — must also check revoked flag)

## Dependencies

### Requires
- 002-user-login (refresh token is issued on login)
- 003-user-logout (revocation must be respected)

### Enables
- Persistent sessions for regular users

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Refresh token for a deactivated user | 401 Unauthorized |
| Malformed refresh token string | 400 Bad Request |

## Out of Scope

- Refresh token rotation — future security enhancement
