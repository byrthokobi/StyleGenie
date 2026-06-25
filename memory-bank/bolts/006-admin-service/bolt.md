---
id: 006-admin-service
unit: 005-admin-service
intent: 001-ai-outfit-recommendation
type: ddd-construction-bolt
status: planned
stories:
  - 001-view-all-users
  - 002-deactivate-user
  - 003-edit-user-account
  - 004-view-platform-activity
created: 2026-06-25T00:00:00Z
started: null
completed: null
current_stage: null
stages_completed: []

requires_bolts: [001-auth-service, 002-auth-service]
enables_bolts: [009-ai-outfit-recommendation-ui]
requires_units: []
blocks: false

complexity:
  avg_complexity: 2
  avg_uncertainty: 1
  max_dependencies: 1
  testing_scope: 2
---

# Bolt: 006-admin-service

## Overview

Admin panel backend — paginated user listing, user deactivation (soft-delete), user account editing, and platform activity log. All endpoints are ADMIN-role protected.

## Objective

Deliver admin-only REST endpoints for user management and activity monitoring, with full RBAC enforcement and soft-delete semantics.

## Stories Included

- **001-view-all-users**: Admin can view paginated list of all users (Must)
- **002-deactivate-user**: Admin can deactivate a user account (Must)
- **003-edit-user-account**: Admin can edit user email or role (Must)
- **004-view-platform-activity**: Admin can view recent activity log (Must)

## Bolt Type

**Type**: DDD Construction Bolt
**Definition**: `.specsmd/aidlc/templates/construction/bolt-types/ddd-construction-bolt.md`

## Stages

- [ ] **1. model**: Domain model — AdminUserView read-model, ActivityLog entity, event types enum
- [ ] **2. design**: Technical design — admin REST endpoints, pagination config, activity log write points in other services
- [ ] **3. implement**: Implementation — admin controller, services, activity log writes (in auth + recommendation services), `@PreAuthorize("hasRole('ADMIN')")` on all endpoints
- [ ] **4. test**: Test report — ADMIN access tests, 403 for REGULAR_USER, deactivation + edit + activity log tests

## Dependencies

### Requires
- 001-auth-service (User entity and authentication)
- 002-auth-service (ADMIN role enforcement)

### Enables
- 009-ai-outfit-recommendation-ui (admin dashboard page consumes this API)

## Success Criteria

- [ ] GET /admin/users returns paginated user list for ADMIN; 403 for REGULAR_USER
- [ ] PATCH /admin/users/{id}/deactivate sets isActive=false; deactivated user cannot log in
- [ ] PATCH /admin/users/{id} updates email/role; rejects duplicate email
- [ ] GET /admin/activity returns event log; 403 for REGULAR_USER
- [ ] Admin cannot deactivate themselves
- [ ] All tests passing

## Notes

Activity log writes must be added to auth-service (USER_REGISTERED, USER_LOGIN) and recommendation-service (RECOMMENDATION_REQUESTED) as part of this bolt's implementation scope.
