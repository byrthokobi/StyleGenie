---
id: 005-delete-wardrobe-item
unit: 003-wardrobe-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 004-wardrobe-service
implemented: false
---

# Story: 005-delete-wardrobe-item

## User Story

**As a** logged-in regular user
**I want** to remove a clothing item from my wardrobe
**So that** items I no longer own are not included in my outfit recommendations

## Acceptance Criteria

- [ ] **Given** I own a wardrobe item, **When** I call DELETE /wardrobe/items/{id}, **Then** the item is removed and I receive a 204 No Content response
- [ ] **Given** I attempt to delete an item I do not own, **When** the request is processed, **Then** I receive a 403 Forbidden
- [ ] **Given** the item ID does not exist, **When** the request is processed, **Then** I receive a 404 Not Found
- [ ] **Given** I am unauthenticated, **When** I call DELETE /wardrobe/items/{id}, **Then** I receive a 401 Unauthorized
- [ ] **Given** the item had a photo, **When** the item is deleted, **Then** the associated photo file is also removed from storage (best-effort)

## Technical Notes

- Hard delete for MVP (no soft-delete / recycle bin)
- Photo cleanup is best-effort; if storage deletion fails, log the error but still return 204
- Deleted item no longer appears in GET /wardrobe/items

## Dependencies

### Requires
- 001-add-wardrobe-item (item must exist)

### Enables
- None

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Delete already-deleted item | 404 Not Found |
| Item referenced in a past recommendation (if history stored) | Delete proceeds; recommendation history may have stale reference |

## Out of Scope

- Undo delete / recycle bin — future enhancement
