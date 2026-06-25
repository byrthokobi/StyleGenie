---
unit: 005-admin-service
intent: 001-ai-outfit-recommendation
phase: inception
status: draft
created: 2026-06-25T00:00:00Z
updated: 2026-06-25T00:00:00Z
---

# Unit Brief: admin-service

## Purpose

Provides admin-only backend capabilities for monitoring and managing the StyleGenie platform — viewing all users, editing or deactivating accounts, and viewing platform activity.

## Scope

### In Scope
- List all registered users with their account status
- Edit a user's account details (e.g., email, role)
- Deactivate (soft-delete) a user account
- View platform activity log (recommendation requests, registrations)

### Out of Scope
- User self-management (handled by respective user-facing endpoints in other units)
- Payment or session management (out of scope for this intent)
- Stylist management (out of scope for this intent)

---

## Assigned Requirements

| FR | Requirement | Priority |
|----|-------------|----------|
| FR-7 | Admins can view all users, edit accounts, deactivate accounts; admin role protected by RBAC | Must |

---

## Domain Concepts

### Key Entities

| Entity | Description | Attributes |
|--------|-------------|------------|
| AdminUserView | Read-model of a user for admin display | id, email, role, isActive, createdAt |
| ActivityLog | Record of significant platform events | id, userId, eventType, timestamp, details |

### Key Operations

| Operation | Description | Inputs | Outputs |
|-----------|-------------|--------|---------|
| listUsers | Return paginated list of all users | page, size, filters | List\<AdminUserView\> |
| getUserDetail | Return full details for a single user | userId | AdminUserView |
| editUser | Update a user's email or role | userId, updated fields | AdminUserView |
| deactivateUser | Soft-delete a user (set isActive = false) | userId | Confirmation |
| viewActivity | Return recent platform activity log | page, size | List\<ActivityLog\> |

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
| 001-view-all-users | Admin can view a paginated list of all users | Must | Planned |
| 002-deactivate-user | Admin can deactivate a user account | Must | Planned |
| 003-edit-user-account | Admin can edit a user's email or role | Must | Planned |
| 004-view-platform-activity | Admin can view recent platform activity log | Must | Planned |

---

## Dependencies

### Depends On
| Unit | Reason |
|------|--------|
| 001-auth-service | Admin role (ADMIN) must be enforced via RBAC |

### Depended By
| Unit | Reason |
|------|--------|
| 006-ai-outfit-recommendation-ui | Displays admin dashboard screens |

### External Dependencies
| System | Purpose | Risk |
|--------|---------|------|
| PostgreSQL | Read user records and activity logs | Low |

---

## Technical Context

### Suggested Technology
- Spring Boot with RBAC enforced via `@PreAuthorize("hasRole('ADMIN')")`
- Pagination via Spring Data Pageable
- Activity log as simple event table in PostgreSQL (no external event bus needed for MVP)

### Integration Points
| Integration | Type | Protocol |
|-------------|------|----------|
| 001-auth-service | Admin role enforcement | Spring Security / JWT |

### Data Storage
| Data | Type | Volume | Retention |
|------|------|--------|-----------|
| User records (read) | PostgreSQL | Shared with auth-service | Permanent |
| Activity logs | PostgreSQL | Low-medium | 90 days rolling (MVP) |

---

## Constraints

- All endpoints in this unit require ADMIN role — no REGULAR_USER access
- Admin role cannot be assigned to self via the edit endpoint (must be set by another admin or via initial seed)
- Deactivation is soft-delete only — no hard deletes from admin panel

---

## Success Criteria

### Functional
- [ ] Admin can list all users with pagination
- [ ] Admin can view detail for a single user
- [ ] Admin can deactivate a user (isActive = false); deactivated user cannot log in
- [ ] Admin can update a user's email or role
- [ ] Admin can view recent activity log entries
- [ ] REGULAR_USER attempting any admin endpoint receives 403

### Non-Functional
- [ ] User list endpoint responds within 500ms p95 for up to 10,000 users
- [ ] Activity log endpoint responds within 500ms p95

### Quality
- [ ] Code coverage > 80%
- [ ] All acceptance criteria met
- [ ] Code reviewed and approved

---

## Bolt Suggestions

| Bolt | Type | Stories | Objective |
|------|------|---------|-----------|
| 006-admin-service | ddd-construction-bolt | 001, 002, 003, 004 | Admin user management + activity monitoring |
