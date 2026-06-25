---
id: 002-upload-wardrobe-item-photo
unit: 003-wardrobe-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 004-wardrobe-service
implemented: false
---

# Story: 002-upload-wardrobe-item-photo

## User Story

**As a** logged-in regular user
**I want** to upload a photo of a wardrobe item
**So that** the AI can visually recognise my clothing when generating outfit recommendations

## Acceptance Criteria

- [ ] **Given** I upload a valid JPG or PNG file (≤ 10MB) for an existing item I own, **When** I call POST /wardrobe/items/{id}/photo, **Then** the photo is stored and I receive a 200 response with the updated photoUrl
- [ ] **Given** I upload an unsupported file type, **When** the request is processed, **Then** I receive a 400 Bad Request
- [ ] **Given** I upload a file larger than 10MB, **When** the request is processed, **Then** I receive a 400 Bad Request
- [ ] **Given** I attempt to upload a photo for an item I do not own, **When** the request is processed, **Then** I receive a 403 Forbidden
- [ ] **Given** I upload a new photo when one already exists, **When** the request is processed, **Then** the old photo is replaced and the new URL is returned

## Technical Notes

- Multipart/form-data upload
- Stored in the same file storage as profile photos
- Item record updated with new photoUrl on success

## Dependencies

### Requires
- 001-add-wardrobe-item (item must exist)

### Enables
- AI recommendation engine (wardrobe item photos are part of AI payload)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Zero-byte file | 400 Bad Request |
| Item ID does not exist | 404 Not Found |

## Out of Scope

- Automatic clothing detection/tagging from photo — future enhancement
