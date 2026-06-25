---
id: 003-view-recommendations
unit: 004-recommendation-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 005-recommendation-service
implemented: false
---

# Story: 003-view-recommendations

## User Story

**As a** logged-in regular user
**I want** to see the outfit combinations returned by the AI after my recommendation request
**So that** I can choose what to wear for my occasion

## Acceptance Criteria

- [ ] **Given** the AI engine returns successful results, **When** the response is returned to the client, **Then** it contains at least 3 outfit combinations with the wardrobe item IDs and display names for each combination
- [ ] **Given** each outfit combination, **When** displayed in the frontend, **Then** the individual item details (name, photo, category) are resolvable from the wardrobe data
- [ ] **Given** the AI engine returns fewer than 3 combinations, **When** the response is returned, **Then** the system returns what was received (no padding) but logs a warning
- [ ] **Given** the response is received successfully, **When** the data is returned to the client, **Then** the response includes the occasion type used and the timestamp of generation

## Technical Notes

- Response format: `{ occasion, generatedAt, combinations: [ { name, items: [ wardrobeItemId, ... ] }, ... ] }`
- Frontend resolves item details from the already-loaded wardrobe list (no additional API call needed per item)

## Dependencies

### Requires
- 002-request-outfit-recommendation (recommendation must be triggered first)

### Enables
- Frontend recommendation results page

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| AI engine returns combination with item not in user's wardrobe | Log warning; return combination as-is (AI may have hallucinated) |

## Out of Scope

- Saving or favouriting outfit combinations — future enhancement
- Sharing outfit combinations — future enhancement
