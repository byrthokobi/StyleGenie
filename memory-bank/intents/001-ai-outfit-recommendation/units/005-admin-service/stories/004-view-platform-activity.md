---
id: 004-view-platform-activity
unit: 005-admin-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 006-admin-service
implemented: false
---

# Story: 004-view-platform-activity

## User Story

**As an** admin
**I want** to view a log of recent platform activity
**So that** I can monitor usage patterns and identify issues

## Acceptance Criteria

- [ ] **Given** I am authenticated as ADMIN, **When** I call GET /admin/activity, **Then** I receive a 200 response with a paginated list of activity log entries including eventType, userId, and timestamp
- [ ] **Given** activity has occurred on the platform, **When** I view the log, **Then** I see entries for key events: user registration, login, recommendation requests
- [ ] **Given** I am authenticated as REGULAR_USER, **When** I call GET /admin/activity, **Then** I receive a 403 Forbidden
- [ ] **Given** I am unauthenticated, **When** I call GET /admin/activity, **Then** I receive a 401 Unauthorized

## Technical Notes

- Activity log is a simple PostgreSQL table: id, userId, eventType, timestamp, details (JSONB)
- Events logged: USER_REGISTERED, USER_LOGIN, RECOMMENDATION_REQUESTED
- Log entries retained for 90 days rolling (scheduled cleanup job for future)
- Default page size: 50 entries, ordered by timestamp descending

## Dependencies

### Requires
- 001-auth-service (ADMIN role required)
- Events are written by auth-service and recommendation-service as side effects

### Enables
- Admin dashboard activity tab in frontend

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| No activity yet | Empty list with 200 OK |
| Activity from deactivated users | Still shown in log (historical record) |

## Out of Scope

- Real-time activity streaming — future enhancement
- Filtering by event type or user — future enhancement
