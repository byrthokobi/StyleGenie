---
id: 004-wardrobe-service
unit: 003-wardrobe-service
intent: 001-ai-outfit-recommendation
type: ddd-construction-bolt
status: planned
stories:
  - 001-add-wardrobe-item
  - 002-upload-wardrobe-item-photo
  - 003-view-wardrobe
  - 004-edit-wardrobe-item
  - 005-delete-wardrobe-item
created: 2026-06-25T00:00:00Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [001-auth-service, 003-profile-service]
enables_bolts: [005-recommendation-service, 008-ai-outfit-recommendation-ui]
requires_units: []
blocks: false

complexity:
  avg_complexity: 2
  avg_uncertainty: 1
  max_dependencies: 2
  testing_scope: 2
---

# Bolt: 004-wardrobe-service

## Overview

Full wardrobe management — users can add, view, edit, delete clothing items and upload photos for each item. This is the data source for the AI recommendation engine.

## Objective

Deliver the wardrobe API with complete CRUD and photo upload for clothing items, enforcing per-user scoping and the 200-item limit.

## Stories Included

- **001-add-wardrobe-item**: User can add a clothing item with category and colour (Must)
- **002-upload-wardrobe-item-photo**: User can upload a photo for a wardrobe item (Must)
- **003-view-wardrobe**: User can browse all their wardrobe items (Must)
- **004-edit-wardrobe-item**: User can edit wardrobe item details (Must)
- **005-delete-wardrobe-item**: User can delete a wardrobe item (Must)

## Bolt Type

**Type**: DDD Construction Bolt
**Definition**: `.specsmd/aidlc/templates/construction/bolt-types/ddd-construction-bolt.md`

## Stages

- [ ] **1. model**: Domain model — WardrobeItem entity, category enum, item-user relationship
- [ ] **2. design**: Technical design — REST endpoints, photo upload, 200-item limit enforcement, ownership check
- [ ] **3. implement**: Implementation — controllers, services, repositories, photo upload via FileStorageService
- [ ] **4. test**: Test report — CRUD tests, ownership enforcement, limit tests, photo upload tests

## Dependencies

### Requires
- 001-auth-service (JWT authentication for all endpoints)
- 003-profile-service (file storage service already established)

### Enables
- 005-recommendation-service (needs wardrobe item list as AI payload input)
- 008-ai-outfit-recommendation-ui (wardrobe page consumes this API)

## Success Criteria

- [ ] POST /wardrobe/items creates item for authenticated user
- [ ] POST /wardrobe/items/{id}/photo stores photo and returns URL
- [ ] GET /wardrobe/items returns only current user's items
- [ ] PUT /wardrobe/items/{id} updates details; returns 403 for wrong user
- [ ] DELETE /wardrobe/items/{id} removes item; returns 403 for wrong user
- [ ] 422 returned when 200-item limit is exceeded
- [ ] All tests passing

## Notes

Re-use `FileStorageService` from profile-service. Ownership checks on edit/delete/photo endpoints are critical for security.
