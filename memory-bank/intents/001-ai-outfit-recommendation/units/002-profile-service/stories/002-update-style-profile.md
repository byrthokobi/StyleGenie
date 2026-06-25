---
id: 002-update-style-profile
unit: 002-profile-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 003-profile-service
implemented: false
---

# Story: 002-update-style-profile

## User Story

**As a** logged-in regular user with an existing style profile
**I want** to update my body shape, face shape, or skin tone selections
**So that** my recommendations stay accurate as my style awareness evolves

## Acceptance Criteria

- [ ] **Given** I have an existing style profile, **When** I call PUT /profile with one or more updated trait values, **Then** my profile is updated and I receive a 200 response with the updated profile
- [ ] **Given** I submit an invalid trait value in the update, **When** the request is processed, **Then** I receive a 400 Bad Request
- [ ] **Given** I have no existing style profile and call PUT /profile, **When** the request is processed, **Then** I receive a 404 Not Found (create first)
- [ ] **Given** I am unauthenticated, **When** I call PUT /profile, **Then** I receive a 401 Unauthorized

## Technical Notes

- Partial update supported (only send fields to change)
- updatedAt timestamp set on every successful update

## Dependencies

### Requires
- 001-create-style-profile (profile must exist)

### Enables
- None directly (same profile used for recommendations)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Update with no fields changed | 200 OK (no-op, still valid) |
| Concurrent updates from two sessions | Last write wins |

## Out of Scope

- Profile history / audit log — future enhancement
