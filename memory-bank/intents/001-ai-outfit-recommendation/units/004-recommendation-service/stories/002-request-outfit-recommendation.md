---
id: 002-request-outfit-recommendation
unit: 004-recommendation-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 005-recommendation-service
implemented: false
---

# Story: 002-request-outfit-recommendation

## User Story

**As a** logged-in regular user with a style profile and wardrobe items
**I want** to request AI outfit recommendations for a selected occasion
**So that** I receive personalised outfit combinations I can actually wear

## Acceptance Criteria

- [ ] **Given** I have a style profile, at least 1 wardrobe item, and I provide a valid occasion, **When** I call POST /recommendations, **Then** the system assembles the prompt data and triggers the Spring AI call
- [ ] **Given** I have no style profile, **When** I call POST /recommendations, **Then** I receive a 422 Unprocessable Entity with message "Style profile required before requesting recommendations"
- [ ] **Given** I have no wardrobe items, **When** I call POST /recommendations, **Then** I receive a 422 Unprocessable Entity with message "Add wardrobe items before requesting recommendations"
- [ ] **Given** I am unauthenticated, **When** I call POST /recommendations, **Then** I receive a 401 Unauthorized

## Technical Notes

- Prompt data assembled internally: styleTraits (bodyShape, faceShape, skinTone) + wardrobeItems (name, category, colour) + occasion
- Profile photo URL optionally included in prompt context if available
- Spring AI ChatClient called synchronously within the request thread (async / streaming is a future enhancement)
- Pre-flight validation checks profile and wardrobe existence before attempting LLM call

## Dependencies

### Requires
- 001-select-occasion (valid occasion must be provided)
- 002-profile-service (style traits needed)
- 003-wardrobe-service (wardrobe items needed)

### Enables
- 003-view-recommendations

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Profile exists but no photo uploaded | Proceed without photo in prompt context |
| Wardrobe has items but none have photos | Proceed using item name/category/colour in prompt |

## Out of Scope

- Saving recommendation history — future enhancement
- Budget-based filtering — future enhancement
