---
id: 002-user-login
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 001-auth-service
implemented: false
---

# Story: 002-user-login

## User Story

**As a** registered user
**I want** to log in with my email and password
**So that** I receive a JWT access token and can use StyleGenie

## Acceptance Criteria

- [ ] **Given** I provide valid credentials, **When** I call POST /auth/login, **Then** I receive a 200 response with an accessToken and a refreshToken
- [ ] **Given** I provide an incorrect password, **When** I call POST /auth/login, **Then** I receive a 401 Unauthorized response
- [ ] **Given** I provide an email that does not exist, **When** I call POST /auth/login, **Then** I receive a 401 Unauthorized (do not reveal whether email exists)
- [ ] **Given** my account is deactivated, **When** I attempt to log in, **Then** I receive a 401 Unauthorized with message "Account is deactivated"
- [ ] **Given** a successful login, **When** I use the returned accessToken on a protected endpoint, **Then** the request is authorised

## Technical Notes

- Access token: JWT, short-lived (15 minutes), contains userId and role
- Refresh token: opaque token stored in DB, 7-day expiry
- Do not differentiate between "wrong password" and "email not found" in error responses (security best practice)

## Dependencies

### Requires
- 001-user-registration (user account must exist)

### Enables
- 003-user-logout
- 004-jwt-token-refresh
- All protected endpoints across all units

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Expired account (future feature) | 401 returned |
| Case-different email (e.g., User@Email.com vs user@email.com) | Treated as same account |

## Out of Scope

- MFA on login — future enhancement
- Account lockout after N failed attempts — future enhancement
