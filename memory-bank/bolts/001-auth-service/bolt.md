---
id: 001-auth-service
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
type: ddd-construction-bolt
status: in-progress
stories:
  - 001-user-registration
  - 002-user-login
  - 003-user-logout
created: 2026-06-25T00:00:00Z
started: 2026-06-25T00:00:00Z
completed: null
current_stage: test
stages_completed:
  - name: model
    completed: 2026-06-25T00:00:00Z
    artifact: ddd-01-domain-model.md
  - name: design
    completed: 2026-06-25T00:00:00Z
    artifact: ddd-02-technical-design.md
  - name: adr
    completed: 2026-06-25T00:00:00Z
    artifacts:
      - adr-001-opaque-refresh-token.md
      - adr-002-dual-id-strategy.md
  - name: implement
    completed: 2026-06-25T00:00:00Z

requires_bolts: []
enables_bolts: [002-auth-service, 003-profile-service, 006-admin-service, 007-ai-outfit-recommendation-ui]
requires_units: []
blocks: false

complexity:
  avg_complexity: 2
  avg_uncertainty: 1
  max_dependencies: 1
  testing_scope: 2
---

# Bolt: 001-auth-service

## Overview

Core authentication implementation — user registration, login, and logout with JWT token issuance and BCrypt password hashing.

## Objective

Deliver a working auth API that lets users register and log in to StyleGenie, receiving JWT access and refresh tokens, and log out to revoke their session.

## Stories Included

- **001-user-registration**: User can register with email and password (Must)
- **002-user-login**: User can log in and receive JWT tokens (Must)
- **003-user-logout**: User can log out and invalidate session (Must)

## Bolt Type

**Type**: DDD Construction Bolt
**Definition**: `.specsmd/aidlc/templates/construction/bolt-types/ddd-construction-bolt.md`

## Stages

- [ ] **1. model**: Domain model — User entity, RefreshToken entity, role enum
- [ ] **2. design**: Technical design — API endpoints, Spring Security config, BCrypt setup
- [ ] **3. implement**: Implementation — controllers, services, repositories, JWT filter
- [ ] **4. test**: Test report — unit + integration tests for registration, login, logout

## Dependencies

### Requires
- None (first bolt — foundational)

### Enables
- 002-auth-service (token refresh + RBAC requires this first)
- 003-profile-service (profile endpoints require auth)
- 006-admin-service (admin endpoints require auth)
- 007-ai-outfit-recommendation-ui (login/register pages consume this API)

## Success Criteria

- [ ] POST /auth/register creates user with hashed password
- [ ] POST /auth/login returns accessToken + refreshToken
- [ ] POST /auth/logout revokes refresh token
- [ ] All tests passing

## Notes

Build order matters: User entity and JWT infrastructure must be established before all other backend units.
