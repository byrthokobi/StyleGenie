---
id: 005-recommendation-service
unit: 004-recommendation-service
intent: 001-ai-outfit-recommendation
type: ddd-construction-bolt
status: planned
stories:
  - 001-select-occasion
  - 002-request-outfit-recommendation
  - 003-view-recommendations
  - 004-spring-ai-integration
created: 2026-06-25T00:00:00Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [003-profile-service, 004-wardrobe-service]
enables_bolts: []
requires_units: []
blocks: false

complexity:
  avg_complexity: 3
  avg_uncertainty: 2
  max_dependencies: 3
  testing_scope: 3
---

# Bolt: 005-recommendation-service

## Overview

AI recommendation orchestration via Spring AI — the core StyleGenie feature. Provides occasion list, builds a structured `PromptTemplate` from user profile and wardrobe data, calls the LLM via Spring AI `ChatClient`, parses the response into outfit combinations, and handles failures gracefully.

## Objective

Deliver the recommendation API end-to-end using Spring AI — from occasion selection through structured LLM prompt to parsed outfit combination response.

## Stories Included

- **001-select-occasion**: User can view predefined occasion types (Must)
- **002-request-outfit-recommendation**: System builds prompt and calls Spring AI on user request (Must)
- **003-view-recommendations**: User receives at least 3 outfit combinations (Must)
- **004-spring-ai-integration**: Spring AI ChatClient + PromptTemplate + response parsing + error handling (Must)

## Bolt Type

**Type**: DDD Construction Bolt
**Definition**: `.specsmd/aidlc/templates/construction/bolt-types/ddd-construction-bolt.md`

## Stages

- [ ] **1. model**: Domain model — RecommendationRequest, OutfitCombination, RecommendationResponse, Occasion enum; Spring AI dependency setup
- [ ] **2. design**: Technical design — REST endpoints, PromptTemplate design, ChatClient config, BeanOutputConverter / JSON parsing strategy, timeout config
- [ ] **3. implement**: Implementation — recommendation controller, orchestration service, Spring AI ChatClient wiring, prompt template, response parser, error handling
- [ ] **4. test**: Test report — happy path with mock ChatModel, LLM failure test, timeout test, pre-flight validation tests

## Dependencies

### Requires
- 003-profile-service (style traits and photo URL needed for prompt)
- 004-wardrobe-service (wardrobe item list needed for prompt)

### Enables
- Nothing (final backend bolt)

## Success Criteria

- [ ] GET /recommendations/occasions returns ≥ 5 occasion types
- [ ] POST /recommendations builds correct prompt and calls Spring AI ChatClient
- [ ] Successful response returns ≥ 3 parsed outfit combinations
- [ ] 422 returned when profile or wardrobe missing
- [ ] 503 returned when LLM provider unavailable or times out
- [ ] All tests passing with mock ChatModel

## Notes

This is the highest-complexity bolt. Key design decision: the PromptTemplate should instruct the LLM to return structured JSON to make parsing reliable. Use `BeanOutputConverter` from Spring AI if available; fall back to manual JSON parsing otherwise. Configure provider via `application.properties` to keep the code provider-agnostic.
