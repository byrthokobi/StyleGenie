---
id: 003-upload-profile-photo
unit: 002-profile-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 003-profile-service
implemented: false
---

# Story: 003-upload-profile-photo

## User Story

**As a** logged-in regular user
**I want** to upload a photo of myself as my profile picture
**So that** StyleGenie can use my image as visual context alongside my style traits in recommendations

## Acceptance Criteria

- [ ] **Given** I upload a valid JPG or PNG file (≤ 5MB), **When** I call POST /profile/photo, **Then** the photo is stored and I receive a 200 response with the photo URL
- [ ] **Given** I upload an unsupported file type (e.g., PDF, GIF), **When** the request is processed, **Then** I receive a 400 Bad Request with message "Only JPG and PNG files are accepted"
- [ ] **Given** I upload a file larger than 5MB, **When** the request is processed, **Then** I receive a 400 Bad Request with message "File size exceeds 5MB limit"
- [ ] **Given** I upload a new photo when one already exists, **When** the request is processed, **Then** the old photo is replaced and the new URL is returned
- [ ] **Given** I am unauthenticated, **When** I call POST /profile/photo, **Then** I receive a 401 Unauthorized

## Technical Notes

- Multipart/form-data upload
- File stored in configured storage (local path for dev, cloud bucket for prod)
- Only file metadata (URL, uploadedAt) stored in PostgreSQL
- Old file should be cleaned up when replaced (best-effort for MVP)

## Dependencies

### Requires
- 001-auth-service (user must be authenticated)
- 001-create-style-profile (profile should exist, though photo can be uploaded independently)

### Enables
- AI recommendation engine (profile photo is part of AI payload)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Zero-byte file | 400 Bad Request |
| Valid file type but corrupted content | 400 or 500 with error message |

## Out of Scope

- Image cropping or resizing in the UI — future enhancement
- Face detection or trait extraction from photo — explicitly out of scope
