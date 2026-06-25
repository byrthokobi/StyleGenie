---
id: 003-view-wardrobe
unit: 003-wardrobe-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 004-wardrobe-service
implemented: false
---

# Story: 003-view-wardrobe

## User Story

**As a** logged-in regular user
**I want** to browse all the clothing items in my wardrobe
**So that** I can see what I have and manage my collection

## Acceptance Criteria

- [ ] **Given** I have wardrobe items, **When** I call GET /wardrobe/items, **Then** I receive a 200 response with a list of all my items including their name, category, colour, and photoUrl
- [ ] **Given** I have no wardrobe items, **When** I call GET /wardrobe/items, **Then** I receive a 200 response with an empty list
- [ ] **Given** I am unauthenticated, **When** I call GET /wardrobe/items, **Then** I receive a 401 Unauthorized
- [ ] **Given** another user has wardrobe items, **When** I retrieve my wardrobe, **Then** I only see my own items

## Technical Notes

- Response includes: id, name, category, colour, photoUrl (nullable), createdAt
- Items returned ordered by createdAt descending (newest first) for MVP
- Pagination optional for MVP; if list ≤ 200 items, return all in one response

## Dependencies

### Requires
- 001-add-wardrobe-item (items must exist to view)

### Enables
- Wardrobe management page in frontend
- Recommendation service (reads wardrobe list before calling AI)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| User has items but some photos missing | Items returned with photoUrl: null for those |

## Out of Scope

- Filtering by category or colour — future enhancement
- Search within wardrobe — future enhancement
