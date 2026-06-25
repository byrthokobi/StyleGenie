---
unit: 003-wardrobe-service
intent: 001-ai-outfit-recommendation
phase: inception
status: draft
created: 2026-06-25T00:00:00Z
updated: 2026-06-25T00:00:00Z
---

# Unit Brief: wardrobe-service

## Purpose

Manages a user's digital wardrobe — the collection of clothing items that feeds into the AI recommendation engine. Each item has a photo, category, colour, and metadata.

## Scope

### In Scope
- Add wardrobe items (with photo, category, colour)
- Upload/replace photo for a wardrobe item
- List all wardrobe items for a user
- Edit wardrobe item details
- Delete wardrobe items

### Out of Scope
- Outfit generation from wardrobe items — handled by `004-recommendation-service`
- Style profile traits — handled by `002-profile-service`

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-4 | Users can add/edit/delete wardrobe items with photo, category, and colour | Must |

---

## Domain Concepts

### Key Entities

| Entity | Description | Attributes |
|--------|-------------|------------|
| WardrobeItem | A single clothing item in a user's wardrobe | id, userId, name, category, colour, photoUrl, createdAt, updatedAt |

### Key Operations

| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| addItem | Create a new wardrobe item with metadata | userId, name, category, colour, photo | WardrobeItem |
| uploadItemPhoto | Upload or replace the photo for an item | itemId, file (binary) | updated photoUrl |
| listItems | Return all wardrobe items for a user | userId | List\<WardrobeItem\> |
| editItem | Update item name, category, or colour | itemId, updated fields | WardrobeItem |
| deleteItem | Remove a wardrobe item | itemId | Confirmation |

---

## Story Summary

| Metric | Count |
|--------|-------|
| Total Stories | 5 |
| Must Have | 5 |
| Should Have | 0 |
| Could Have | 0 |

### Stories

| Story ID | Title | Priority | Status |
|----------|-------|----------|--------|
| 001-add-wardrobe-item | User can add a clothing item with category and colour | Must | Planned |
| 002-upload-wardrobe-item-photo | User can upload a photo for a wardrobe item | Must | Planned |
| 003-view-wardrobe | User can browse all their wardrobe items | Must | Planned |
| 004-edit-wardrobe-item | User can edit the details of a wardrobe item | Must | Planned |
| 005-delete-wardrobe-item | User can delete a wardrobe item | Must | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| 001-auth-service | User must be authenticated; items scoped to user ID |
| 002-profile-service | User should have a style profile before building wardrobe |

### Depended By
| Unit | Reason |
|------|--------|
| 004-recommendation-service | Needs the full wardrobe item list (with photo URLs) as input to AI engine |
| 006-ai-outfit-recommendation-ui | Displays wardrobe management screens |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| PostgreSQL | Persist WardrobeItem records | Low |
| File Storage | Store wardrobe item photos | Medium (provider TBD) |

---

## Technical Context

### Suggested Technology
- Spring Boot + Spring Data JPA
- Multipart upload for item photos
- Categories as enum: TOP, BOTTOM, SHOES, ACCESSORY, OUTERWEAR, DRESS

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| 001-auth-service | JWT validation | Spring Security filter |
| File Storage | Photo upload/retrieval | HTTP / File IO |
| 004-recommendation-service | Provides wardrobe item list | Internal REST call |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| WardrobeItem records | PostgreSQL | Up to 200 per user | Permanent until deleted |
| Item photos | File Storage | Small-medium (≤ 10MB each) | Permanent until item deleted |

---

## Constraints

- Category must be from a predefined enum (TOP, BOTTOM, SHOES, ACCESSORY, OUTERWEAR, DRESS)
- Max 200 wardrobe items per user (MVP limit)
- Item photo: JPG or PNG, max 10MB
- Users can only access their own wardrobe items (no cross-user access)

---

## Success Criteria

### Functional
- [ ] User can add a wardrobe item and receive 201 Created with item data
- [ ] User can upload a photo for an existing item and receive updated photoUrl
- [ ] User can list all their wardrobe items
- [ ] User can edit item name/category/colour
- [ ] User can delete an item and it no longer appears in listing

### Non-Functional
- [ ] List wardrobe responds within 500ms p95 for up to 200 items
- [ ] Photo upload responds within 3 seconds p95

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 004-wardrobe-service | ddd-construction-bolt | 001, 002, 003, 004, 005 | Full wardrobe CRUD with photo upload |
