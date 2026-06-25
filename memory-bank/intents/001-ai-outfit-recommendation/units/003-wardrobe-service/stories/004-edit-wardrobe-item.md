---
id: 004-edit-wardrobe-item
unit: 003-wardrobe-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 004-wardrobe-service
implemented: false
---

# Story: 004-edit-wardrobe-item

## User Story

**As a** logged-in regular user
**I want** to edit the name, category, or colour of a wardrobe item
**So that** I can correct mistakes or update the details of my clothing

## Acceptance Criteria

- [ ] **Given** I own an existing wardrobe item, **When** I call PUT /wardrobe/items/{id} with updated fields, **Then** the item is updated and I receive a 200 response with the updated item
- [ ] **Given** I submit an invalid category value, **When** the request is processed, **Then** I receive a 400 Bad Request
- [ ] **Given** I attempt to edit an item I do not own, **When** the request is processed, **Then** I receive a 403 Forbidden
- [ ] **Given** the item ID does not exist, **When** the request is processed, **Then** I receive a 404 Not Found
- [ ] **Given** I am unauthenticated, **When** I call PUT /wardrobe/items/{id}, **Then** I receive a 401 Unauthorized

## Technical Notes

- Partial update (PATCH semantics): only send fields to change
- Photo is not updated via this endpoint (use dedicated photo upload endpoint)
- updatedAt timestamp set on every successful update

## Dependencies

### Requires
- 001-add-wardrobe-item (item must exist)

### Enables
- None directly

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Updating with no fields changed | 200 OK (no-op) |
| Updating name to empty string | 400 Bad Request |

## Out of Scope

- Bulk edit of multiple items — future enhancement
