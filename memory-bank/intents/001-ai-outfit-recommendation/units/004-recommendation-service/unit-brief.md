---
unit: 004-recommendation-service
intent: 001-ai-outfit-recommendation
phase: inception
status: draft
created: 2026-06-25T00:00:00Z
updated: 2026-06-25T00:00:00Z
---

# Unit Brief: recommendation-service

## Purpose

Orchestrates AI outfit recommendations using **Spring AI** — accepts an occasion input from the user, assembles a structured prompt from the user's style traits, profile photo reference, and wardrobe items, calls the LLM via Spring AI's `ChatClient`, and returns outfit combinations.

## Scope

### In Scope
- Occasion selection (user picks from a predefined list)
- Building a structured LLM prompt (traits + occasion + wardrobe item descriptions)
- Calling the LLM via Spring AI `ChatClient` / `PromptTemplate`
- Parsing the LLM response into structured outfit combinations
- Graceful fallback when the LLM provider is unavailable or times out

### Out of Scope
- External Python AI service — AI runs in-process via Spring AI
- Storing recommendation history (out of MVP scope)
- Wardrobe or profile data management — upstream units

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-5 | User selects an occasion type before requesting recommendations | Must |
| FR-6 | System generates outfit combinations using Spring AI based on traits + wardrobe + occasion | Must |

---

## Domain Concepts

### Key Entities

| Entity | Description | Attributes |
|--------|-------------|------------|
| RecommendationRequest | Input assembled for the LLM prompt | userId, occasion, styleTraits, wardrobeItems[] |
| OutfitCombination | A single AI-generated outfit suggestion | itemIds[], displayName, styleNote |
| RecommendationResponse | Full structured response | combinations[], occasion, generatedAt |

### Key Operations

| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| getOccasions | Return predefined occasion types | — | List\<Occasion\> |
| requestRecommendation | Assemble prompt, call Spring AI, parse response | userId, occasion | RecommendationResponse |
| buildPrompt | Construct PromptTemplate with user data | RecommendationRequest | Prompt |
| parseAiResponse | Parse LLM text response into OutfitCombination list | String (LLM output) | OutfitCombination[] |
| handleAiFailure | Return user-friendly error when LLM unavailable | exception | Error response |

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
| 001-select-occasion | User can view and select from predefined occasion types | Must | Planned |
| 002-request-outfit-recommendation | System builds prompt and calls Spring AI on user request | Must | Planned |
| 003-view-recommendations | User receives at least 3 outfit combinations | Must | Planned |
| 004-spring-ai-integration | Spring AI ChatClient integration with prompt design and error handling | Must | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| 001-auth-service | User must be authenticated |
| 002-profile-service | Needs style traits (bodyShape, faceShape, skinTone) and profilePhotoUrl |
| 003-wardrobe-service | Needs wardrobe item list (names, categories, colours) |

### Depended By
| Unit | Reason |
|------|--------|
| None | Leaf service |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| LLM Provider (via Spring AI) | Generates outfit combinations from prompt | High (LLM latency, API availability) |

---

## Technical Context

### Suggested Technology
- **Spring AI** (`spring-ai-openai-spring-boot-starter` or equivalent provider starter)
- `ChatClient` / `ChatModel` for sending prompts
- `PromptTemplate` for structured, parameterised prompt construction
- Provider configured via `application.properties` (`spring.ai.openai.api-key`, etc.)

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| 001-auth-service | JWT validation | Spring Security filter |
| 002-profile-service | Internal data fetch | Service/repository call |
| 003-wardrobe-service | Internal data fetch | Service/repository call |
| LLM Provider | Spring AI ChatClient | HTTPS (handled by Spring AI) |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| Recommendation logs (optional) | PostgreSQL | Low | Short-term |

---

## Constraints

- Occasion types are predefined; no free-text occasion input accepted
- LLM response must be parsed into structured outfit combinations — prompt must request structured output (e.g. JSON-formatted response)
- Total recommendation request must complete within 8 seconds; Spring AI timeout must be configured accordingly
- LLM provider credentials stored in environment variables / Spring config — never hardcoded

---

## Success Criteria

### Functional
- [ ] GET /recommendations/occasions returns ≥ 5 occasion types
- [ ] POST /recommendations builds correct prompt and calls Spring AI
- [ ] Response includes ≥ 3 outfit combinations on success
- [ ] 422 returned when profile or wardrobe missing
- [ ] 503 returned when LLM provider is unavailable or times out

### Non-Functional
- [ ] End-to-end recommendation response < 8 seconds p95
- [ ] LLM timeout handled gracefully — no raw exception exposed to client

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 005-recommendation-service | ddd-construction-bolt | 001, 002, 003, 004 | Occasion list + Spring AI prompt + recommendation orchestration |
