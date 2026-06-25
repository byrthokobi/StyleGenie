---
id: 002-auth-service
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
type: ddd-construction-bolt
status: planned
stories:
  - 004-jwt-token-refresh
  - 005-role-based-access-control
created: 2026-06-25T00:00:00Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [001-auth-service]
enables_bolts: [006-admin-service]
requires_units: []
blocks: false

complexity:
  avg_complexity: 2
  avg_uncertainty: 1
  max_dependencies: 1
  testing_scope: 2
---

# Bolt: 002-auth-service

## Overview

Token refresh and role-based access control — extends the auth service with JWT refresh endpoint and Spring Security RBAC enforcement across all protected routes.

## Objective

Implement JWT token refresh and harden all endpoints with RBAC so REGULAR_USER and ADMIN roles are properly enforced.

## Stories Included

- **004-jwt-token-refresh**: User can refresh access token using refresh token (Must)
- **005-role-based-access-control**: Endpoints enforce REGULAR_USER and ADMIN roles (Must)

## Bolt Type

**Type**: DDD Construction Bolt
**Definition**: `.specsmd/aidlc/templates/construction/bolt-types/ddd-construction-bolt.md`

## Stages

- [ ] **1. model**: Extend domain — role enforcement model, token refresh flow
- [ ] **2. design**: Technical design — /auth/refresh endpoint, Spring Security method-level security
- [ ] **3. implement**: Implementation — refresh endpoint, `@PreAuthorize` annotations, role claim in JWT
- [ ] **4. test**: Test report — refresh scenarios, 401/403 enforcement tests

## Dependencies

### Requires
- 001-auth-service (User entity and JWT infrastructure must exist)

### Enables
- 006-admin-service (ADMIN role enforcement is a prerequisite)

## Success Criteria

- [ ] POST /auth/refresh returns new access token for valid refresh token
- [ ] 401 returned for unauthenticated requests on protected endpoints
- [ ] 403 returned for REGULAR_USER on ADMIN endpoints
- [ ] All tests passing

## Notes

RBAC configuration here applies globally — all subsequent units depend on this being correct.
