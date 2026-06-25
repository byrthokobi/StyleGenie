---
id: 004-view-style-profile
unit: 002-profile-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 003-profile-service
implemented: false
---

# Story: 004-view-style-profile

## User Story

**As a** logged-in regular user
**I want** to view my current style profile including my trait selections and profile photo
**So that** I can review and confirm my profile before requesting recommendations

## Acceptance Criteria

- [ ] **Given** I have a style profile, **When** I call GET /profile, **Then** I receive a 200 response with my bodyShape, faceShape, skinTone, and profilePhotoUrl
- [ ] **Given** I have no style profile yet, **When** I call GET /profile, **Then** I receive a 404 Not Found
- [ ] **Given** I have a profile but no photo uploaded, **When** I call GET /profile, **Then** profilePhotoUrl is null in the response
- [ ] **Given** I am unauthenticated, **When** I call GET /profile, **Then** I receive a 401 Unauthorized

## Technical Notes

- Returns combined view of StyleProfile + ProfilePhoto in a single response object
- profilePhotoUrl is a resolvable URL (not a file path)

## Dependencies

### Requires
- 001-create-style-profile (profile must exist)

### Enables
- Frontend display of profile on StyleProfilePage

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Photo stored but file missing from storage | Return profile data with photoUrl; storage error logged separately |

## Out of Scope

- Public profile view for other users — not applicable (profiles are private)
