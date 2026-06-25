---
id: 001-create-style-profile
unit: 002-profile-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 003-profile-service
implemented: false
---

# Story: 001-create-style-profile

## User Story

**As a** logged-in regular user
**I want** to select my body shape, face shape, and skin tone from predefined options
**So that** StyleGenie can use my traits to generate personalised outfit recommendations

## Acceptance Criteria

- [ ] **Given** I am authenticated, **When** I call POST /profile with valid bodyShape, faceShape, and skinTone values, **Then** my style profile is created and I receive a 201 response with the saved profile
- [ ] **Given** I submit an invalid bodyShape value (not in the allowed enum), **When** the request is processed, **Then** I receive a 400 Bad Request with a validation error
- [ ] **Given** I already have a style profile and call POST /profile again, **When** the request is processed, **Then** I receive a 409 Conflict (use update endpoint instead)
- [ ] **Given** I am unauthenticated, **When** I call POST /profile, **Then** I receive a 401 Unauthorized

## Technical Notes

- Body shape enum: HOURGLASS, PEAR, APPLE, RECTANGLE, INVERTED_TRIANGLE
- Face shape enum: OVAL, ROUND, SQUARE, HEART, OBLONG
- Skin tone enum: FAIR, LIGHT, MEDIUM, OLIVE, TAN, DARK
- Profile linked to authenticated userId from JWT

## Dependencies

### Requires
- 001-auth-service (user must be authenticated)

### Enables
- 002-update-style-profile
- 003-upload-profile-photo
- 004-view-style-profile
- 004-recommendation-service (needs profile to exist)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Missing one of three trait fields | 400 Bad Request |
| All three fields are valid enum values | 201 Created |

## Out of Scope

- AI-based trait detection from photo — manual selection only
