---
id: 002-deactivate-user
unit: 005-admin-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 006-admin-service
implemented: false
---

# Story: 002-deactivate-user

## User Story

**As an** admin
**I want** to deactivate a user account
**So that** the user can no longer log in or use the platform

## Acceptance Criteria

- [ ] **Given** I am authenticated as ADMIN and provide a valid user ID, **When** I call PATCH /admin/users/{id}/deactivate, **Then** the user's isActive is set to false and I receive a 200 response
- [ ] **Given** the deactivated user attempts to log in, **When** the login is processed, **Then** they receive a 401 Unauthorized with message "Account is deactivated"
- [ ] **Given** I attempt to deactivate an already-deactivated user, **When** the request is processed, **Then** I receive a 200 OK (idempotent)
- [ ] **Given** I am authenticated as REGULAR_USER, **When** I call PATCH /admin/users/{id}/deactivate, **Then** I receive a 403 Forbidden
- [ ] **Given** the user ID does not exist, **When** the request is processed, **Then** I receive a 404 Not Found

## Technical Notes

- Soft-delete only (isActive = false); no data is deleted
- All existing refresh tokens for the deactivated user should be revoked
- Admin cannot deactivate their own account via this endpoint

## Dependencies

### Requires
- 001-view-all-users (admin sees users before deactivating)
- 002-user-login in auth-service (login checks isActive)

### Enables
- None

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Admin tries to deactivate themselves | 422 Unprocessable Entity |
| Deactivated user's existing JWT | JWT remains technically valid for up to 15 min (access token lifetime); refresh is blocked immediately |

## Out of Scope

- Reactivating a user account — include in this story as PATCH /admin/users/{id}/activate
