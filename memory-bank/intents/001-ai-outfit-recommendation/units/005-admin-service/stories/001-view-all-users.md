---
id: 001-view-all-users
unit: 005-admin-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 006-admin-service
implemented: false
---

# Story: 001-view-all-users

## User Story

**As an** admin
**I want** to view a paginated list of all registered users
**So that** I can monitor who is using the platform

## Acceptance Criteria

- [ ] **Given** I am authenticated as ADMIN, **When** I call GET /admin/users, **Then** I receive a 200 response with a paginated list of users including their id, email, role, isActive, and createdAt
- [ ] **Given** I pass page and size query parameters, **When** the request is processed, **Then** the correct page of users is returned
- [ ] **Given** I am authenticated as REGULAR_USER, **When** I call GET /admin/users, **Then** I receive a 403 Forbidden
- [ ] **Given** I am unauthenticated, **When** I call GET /admin/users, **Then** I receive a 401 Unauthorized

## Technical Notes

- Default page size: 20 users per page
- Response includes pagination metadata: totalElements, totalPages, currentPage
- Does not return password hashes or refresh tokens in the response

## Dependencies

### Requires
- 001-auth-service (ADMIN role required)

### Enables
- 002-deactivate-user
- 003-edit-user-account
- Frontend admin dashboard user table

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| No users registered | Empty list with 200 OK |
| Very large page size requested (e.g., 10000) | Capped at 100 per page |

## Out of Scope

- Filtering users by role or status via query params — future enhancement
- Exporting user list to CSV — future enhancement
