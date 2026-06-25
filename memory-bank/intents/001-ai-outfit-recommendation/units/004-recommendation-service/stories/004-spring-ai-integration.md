---
id: 004-spring-ai-integration
unit: 004-recommendation-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 005-recommendation-service
implemented: false
---

# Story: 004-spring-ai-integration

## User Story

**As a** developer
**I want** the recommendation service to use Spring AI's ChatClient to call an LLM with a structured prompt and parse the response into outfit combinations
**So that** the AI recommendation feature works reliably and degrades gracefully on failure

## Acceptance Criteria

- [ ] **Given** the LLM provider is configured and available, **When** a recommendation is requested, **Then** Spring AI's `ChatClient` sends the assembled prompt and returns a parseable response
- [ ] **Given** the LLM response is received, **When** it is parsed, **Then** it yields a list of `OutfitCombination` objects each containing item references and a style note
- [ ] **Given** the LLM provider is unavailable or the API key is invalid, **When** the call fails, **Then** the user receives a 503 with message "Recommendation service is temporarily unavailable. Please try again shortly."
- [ ] **Given** the Spring AI call exceeds 8 seconds, **When** the timeout occurs, **Then** the request is cancelled and the user receives the 503 response
- [ ] **Given** the LLM returns a malformed or unparseable response, **When** parsing fails, **Then** the error is logged and the user receives the 503 response

## Technical Notes

- Use Spring AI `PromptTemplate` to build a structured prompt with placeholders: `{occasion}`, `{bodyShape}`, `{faceShape}`, `{skinTone}`, `{wardrobeItems}`
- Prompt should instruct the LLM to return JSON-formatted outfit combinations (3+ combinations, each listing item names/categories and a short style note)
- Parse LLM response using a `BeanOutputConverter` or manual JSON parsing
- LLM provider configured via `application.properties` (e.g. `spring.ai.openai.api-key=${OPENAI_API_KEY}`)
- Timeout configured via Spring AI or underlying HTTP client properties
- Use a mock `ChatModel` in tests — do not call the real LLM in unit/integration tests

## Dependencies

### Requires
- 002-request-outfit-recommendation (this integration is invoked from that story)

### Enables
- 003-view-recommendations (results flow through this integration)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| LLM returns valid JSON but with fewer than 3 combinations | Return what was received; log warning |
| LLM API key not configured at startup | Application fails fast with clear config error |
| Prompt exceeds LLM context window | Log error; return 503 to user |

## Out of Scope

- Fine-tuning or training the LLM — use a general-purpose model with a well-crafted prompt
- Switching between multiple LLM providers at runtime — single provider for MVP
