---
id: 001-add-wardrobe-item
unit: 003-wardrobe-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 004-wardrobe-service
implemented: false
---

# Story: 001-add-wardrobe-item

## User Story

**As a** logged-in regular user
**I want** to add a clothing item to my digital wardrobe with its name, category, and colour
**So that** it becomes available as input for AI outfit recommendations

## Acceptance Criteria

- [ ] **Given** I provide a valid name, category, and colour, **When** I call POST /wardrobe/items, **Then** the item is created and I receive a 201 response with the item ID and details
- [ ] **Given** I provide an invalid category (not in enum), **When** the request is processed, **Then** I receive a 400 Bad Request
- [ ] **Given** I already have 200 wardrobe items, **When** I attempt to add another, **Then** I receive a 422 Unprocessable Entity with message "Wardrobe item limit reached (200)"
- [ ] **Given** I am unauthenticated, **When** I call POST /wardrobe/items, **Then** I receive a 401 Unauthorized

## Technical Notes

- Category enum: TOP, BOTTOM, SHOES, ACCESSORY, OUTERWEAR, DRESS
- Colour: free-text string (e.g., "Navy Blue") — no enum restriction for MVP
- Item created without a photo initially (photo upload is a separate step)
- Item is always scoped to the authenticated user

## Dependencies

### Requires
- 001-auth-service (user must be authenticated)

### Enables
- 002-upload-wardrobe-item-photo (item must exist before uploading photo)
- 003-view-wardrobe
- 004-edit-wardrobe-item
- 005-delete-wardrobe-item

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Name is empty string | 400 Bad Request |
| Colour field missing | 400 Bad Request |
| Adding 201st item | 422 with limit message |

## Out of Scope

- Barcode or brand lookup — future enhancement
- Season/occasion tagging on items — future enhancement
