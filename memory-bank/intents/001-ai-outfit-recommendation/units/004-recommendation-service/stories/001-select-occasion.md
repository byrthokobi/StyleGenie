---
id: 001-select-occasion
unit: 004-recommendation-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 005-recommendation-service
implemented: false
---

# Story: 001-select-occasion

## User Story

**As a** logged-in regular user
**I want** to view a list of available occasion types and select one
**So that** I can tell StyleGenie what kind of event I am dressing for before requesting recommendations

## Acceptance Criteria

- [ ] **Given** I am authenticated, **When** I call GET /recommendations/occasions, **Then** I receive a 200 response with a list of at least 5 occasion types
- [ ] **Given** I select a valid occasion type, **When** I submit it as part of a recommendation request, **Then** it is accepted and processed
- [ ] **Given** I submit an occasion type not in the predefined list, **When** the request is processed, **Then** I receive a 400 Bad Request
- [ ] **Given** I am unauthenticated, **When** I call GET /recommendations/occasions, **Then** I receive a 401 Unauthorized

## Technical Notes

- Occasions are server-defined constants (not stored in DB for MVP): CASUAL, FORMAL, BUSINESS, DATE_NIGHT, WEDDING, PARTY
- Returned as an array of objects: `{ id: "CASUAL", displayName: "Casual" }`

## Dependencies

### Requires
- 001-auth-service (authentication required)

### Enables
- 002-request-outfit-recommendation (occasion is a required input)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Request with no occasion (empty string) | 400 Bad Request from recommendation endpoint |

## Out of Scope

- Custom/user-defined occasions — future enhancement
- Multi-occasion selection — future enhancement
