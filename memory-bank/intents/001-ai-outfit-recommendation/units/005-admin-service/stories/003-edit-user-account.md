---
id: 003-edit-user-account
unit: 005-admin-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 006-admin-service
implemented: false
---

# Story: 003-edit-user-account

## User Story

**As an** admin
**I want** to edit a user's email address or role
**So that** I can correct account details or adjust access levels

## Acceptance Criteria

- [ ] **Given** I am ADMIN and provide a valid user ID with a new email, **When** I call PATCH /admin/users/{id}, **Then** the user's email is updated and I receive a 200 response with the updated user
- [ ] **Given** I provide a new email that is already taken by another user, **When** the request is processed, **Then** I receive a 409 Conflict
- [ ] **Given** I provide an invalid role value, **When** the request is processed, **Then** I receive a 400 Bad Request
- [ ] **Given** I attempt to change a user's role to ADMIN, **When** the request is processed, **Then** the change succeeds (admin can grant admin role)
- [ ] **Given** I am authenticated as REGULAR_USER, **When** I call PATCH /admin/users/{id}, **Then** I receive a 403 Forbidden
- [ ] **Given** the user ID does not exist, **When** the request is processed, **Then** I receive a 404 Not Found

## Technical Notes

- Partial update: only send fields to change (email and/or role)
- Role values: REGULAR_USER, ADMIN
- Changing email does not force re-login for the affected user (JWT remains valid until expiry)

## Dependencies

### Requires
- 001-view-all-users (admin views users before editing)

### Enables
- None

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Admin edits their own role to REGULAR_USER | Allowed — but admin loses admin access after next login |
| Email update to same value as current | 200 OK (no-op) |

## Out of Scope

- Admin resetting a user's password — future enhancement
