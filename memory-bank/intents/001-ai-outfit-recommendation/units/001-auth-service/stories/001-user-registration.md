---
id: 001-user-registration
unit: 001-auth-service
intent: 001-ai-outfit-recommendation
status: draft
priority: must
created: 2026-06-25T00:00:00Z
assigned_bolt: 001-auth-service
implemented: false
---

# Story: 001-user-registration

## User Story

**As a** new user
**I want** to register with my email address and a password
**So that** I can create an account and access StyleGenie

## Acceptance Criteria

- [ ] **Given** I submit a valid email and password (≥ 8 characters), **When** I call POST /auth/register, **Then** my account is created and I receive a 201 response with my user ID and email
- [ ] **Given** I submit an email that is already registered, **When** I call POST /auth/register, **Then** I receive a 409 Conflict response with error message "Email already in use"
- [ ] **Given** I submit a password shorter than 8 characters, **When** I call POST /auth/register, **Then** I receive a 400 Bad Request with a validation error message
- [ ] **Given** I submit an invalid email format, **When** I call POST /auth/register, **Then** I receive a 400 Bad Request with a validation error message
- [ ] **Given** a successful registration, **When** my account is stored, **Then** my password is hashed (BCrypt) and never stored in plain text

## Technical Notes

- Password hashed with BCrypt (cost factor ≥ 10)
- Email stored in lowercase, trimmed
- Default role assigned: REGULAR_USER
- Registration does not auto-login (separate login step required)

## Dependencies

### Requires
- None (first story in auth service)

### Enables
- 002-user-login (user must exist before they can log in)

## Edge Cases

| Scenario | Expected Behavior |
|----------|-------------------|
| Whitespace-only password | Rejected as invalid (fails min length) |
| Email with leading/trailing spaces | Trimmed and stored as lowercase |
| Duplicate registration attempt (race condition) | 409 returned; only one account created |

## Out of Scope

- Email verification after registration — future enhancement
- Social login — future enhancement
