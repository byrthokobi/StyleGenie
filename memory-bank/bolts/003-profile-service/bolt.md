---
id: 003-profile-service
unit: 002-profile-service
intent: 001-ai-outfit-recommendation
type: ddd-construction-bolt
status: planned
stories:
  - 001-create-style-profile
  - 002-update-style-profile
  - 003-upload-profile-photo
  - 004-view-style-profile
created: 2026-06-25T00:00:00Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [001-auth-service]
enables_bolts: [004-wardrobe-service, 005-recommendation-service, 008-ai-outfit-recommendation-ui]
requires_units: []
blocks: false

complexity:
  avg_complexity: 2
  avg_uncertainty: 2
  max_dependencies: 2
  testing_scope: 2
---

# Bolt: 003-profile-service

## Overview

Full style profile and photo management — users can create/update their body shape, face shape, and skin tone selections, upload a profile photo, and view their complete profile.

## Objective

Deliver the profile API that stores user style traits and profile photo, enabling the recommendation engine to access this data as input.

## Stories Included

- **001-create-style-profile**: User can select and save body shape, face shape, skin tone (Must)
- **002-update-style-profile**: User can edit existing style profile (Must)
- **003-upload-profile-photo**: User can upload and replace profile photo (Must)
- **004-view-style-profile**: User can view current style profile and photo (Must)

## Bolt Type

**Type**: DDD Construction Bolt
**Definition**: `.specsmd/aidlc/templates/construction/bolt-types/ddd-construction-bolt.md`

## Stages

- [ ] **1. model**: Domain model — StyleProfile entity, ProfilePhoto entity, trait enums (bodyShape, faceShape, skinTone)
- [ ] **2. design**: Technical design — REST endpoints, multipart upload handling, file storage abstraction
- [ ] **3. implement**: Implementation — controllers, services, repositories, file storage service
- [ ] **4. test**: Test report — profile CRUD + photo upload tests

## Dependencies

### Requires
- 001-auth-service (JWT authentication required for all endpoints)

### Enables
- 004-wardrobe-service (wardrobe scoped to user with profile)
- 005-recommendation-service (style traits are AI payload inputs)
- 008-ai-outfit-recommendation-ui (profile page consumes this API)

## Success Criteria

- [ ] POST /profile creates profile with valid enum values
- [ ] PUT /profile updates existing profile
- [ ] POST /profile/photo accepts JPG/PNG ≤ 5MB and returns URL
- [ ] GET /profile returns traits + photoUrl
- [ ] All tests passing

## Notes

File storage abstraction is critical — implement a `FileStorageService` interface so local and cloud implementations are interchangeable.
