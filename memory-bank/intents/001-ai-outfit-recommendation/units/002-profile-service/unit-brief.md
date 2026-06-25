---
unit: 002-profile-service
intent: 001-ai-outfit-recommendation
phase: inception
status: draft
created: 2026-06-25T00:00:00Z
updated: 2026-06-25T00:00:00Z
---

# Unit Brief: profile-service

## Purpose

Manages a user's style profile — their manually selected physical traits (body shape, face shape, skin tone) and their profile photo. This data is the primary input to the AI recommendation engine alongside wardrobe items.

## Scope

### In Scope
- Create and update style profile (body shape, face shape, skin tone selections)
- Upload and replace user profile photo
- Retrieve style profile for display and for passing to recommendation engine

### Out of Scope
- Automatic trait detection from photo — all selections are manual
- Wardrobe items — handled by `003-wardrobe-service`
- Recommendation logic — handled by `004-recommendation-service`

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-2 | Users manually select body shape, face shape, skin tone from predefined options | Must |
| FR-3 | Users can upload a personal profile photo used as visual context | Must |

---

## Domain Concepts

### Key Entities

| Entity | Description | Attributes |
|--------|-------------|------------|
| StyleProfile | User's style trait selections | id, userId, bodyShape, faceShape, skinTone, createdAt, updatedAt |
| ProfilePhoto | Uploaded profile photo metadata | id, userId, fileUrl, uploadedAt |

### Key Operations

| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| createProfile | Save initial style trait selections | userId, bodyShape, faceShape, skinTone | StyleProfile |
| updateProfile | Update existing trait selections | userId, updated fields | StyleProfile |
| uploadProfilePhoto | Store profile photo file and metadata | userId, file (binary) | ProfilePhoto with URL |
| getProfile | Retrieve full style profile + photo URL | userId | StyleProfile + photoUrl |

---

## Story Summary

| Metric | Count |
|--------|-------|
| Total Stories | 4 |
| Must Have | 4 |
| Should Have | 0 |
| Could Have | 0 |

### Stories

| Story ID | Title | Priority | Status |
|----------|-------|----------|--------|
| 001-create-style-profile | User can select and save body shape, face shape, skin tone | Must | Planned |
| 002-update-style-profile | User can edit existing style profile selections | Must | Planned |
| 003-upload-profile-photo | User can upload and replace their profile photo | Must | Planned |
| 004-view-style-profile | User can view their current style profile and photo | Must | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| 001-auth-service | User must be authenticated; profile linked to user ID |

### Depended By
| Unit | Reason |
|------|--------|
| 004-recommendation-service | Needs style traits and profile photo URL for AI input |
| 006-ai-outfit-recommendation-ui | Displays profile setup screens |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| PostgreSQL | Persist StyleProfile and ProfilePhoto records | Low |
| File Storage | Store profile photo binary | Medium (provider TBD) |

---

## Technical Context

### Suggested Technology
- Spring Boot + Spring Data JPA for persistence
- Multipart file upload via Spring MVC
- File stored to configured storage (local path or cloud bucket)

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| 001-auth-service | JWT validation | Spring Security filter |
| File Storage | Image upload/retrieval | HTTP / File IO |
| 004-recommendation-service | Provides style traits | Internal REST call |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| StyleProfile | PostgreSQL | 1 record per user | Permanent |
| ProfilePhoto metadata | PostgreSQL | 1 per user | Permanent |
| Profile photo file | File Storage | Small (< 5MB each) | Permanent |

---

## Constraints

- Body shape, face shape, and skin tone values must be from predefined enumerations (not free text)
- Supported photo formats: JPG, PNG; max file size: 5MB
- Each user has at most one active style profile and one active profile photo

---

## Success Criteria

### Functional
- [ ] User can select and save all three trait types and receive 200/201
- [ ] User can update selections and changes persist
- [ ] User can upload a photo (JPG/PNG ≤ 5MB) and receive back a URL
- [ ] User can retrieve full profile (traits + photo URL) via GET

### Non-Functional
- [ ] Profile GET responds within 500ms p95
- [ ] Photo upload responds within 3 seconds p95

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 003-profile-service | ddd-construction-bolt | 001, 002, 003, 004 | Full style profile + photo management |
