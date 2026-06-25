---
bolt: 001-auth-service
created: 2026-06-25T00:00:00Z
status: accepted
superseded_by: null
---

# ADR-001: Opaque UUID Refresh Token (Database-Backed) vs Signed JWT Refresh Token

## Context

StyleGenie issues both a short-lived access token (JWT, 15 min) and a long-lived refresh token (7 days) on login. The refresh token needs to support revocation — specifically, a user logging out must immediately invalidate their session, and an admin deactivating a user must also kill all active sessions.

There are two mainstream approaches for refresh tokens: a signed JWT (self-contained, no DB lookup) or an opaque random value stored in the database (requires DB lookup on every refresh).

## Decision

Use an **opaque UUID** as the refresh token, stored in the `refresh_tokens` table, validated against the database on every use.

## Rationale

The core requirement is **immediate revocation**. Signed JWT refresh tokens cannot be revoked without maintaining a denylist (which reintroduces a database lookup anyway). Since we need a DB lookup regardless of the token format, the signed JWT adds complexity (signing key management, claim parsing) without any benefit.

An opaque UUID is simpler, auditable, and gives us native revocation via a `revoked` flag and user-level bulk revocation (`revokeByUserId`) for deactivation flows.

### Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| Signed JWT refresh token (no DB) | Stateless, no DB lookup on refresh | Cannot be revoked without a denylist; denylist re-introduces DB lookup; added complexity | Revocation is a hard requirement — the stateless benefit disappears |
| Signed JWT refresh token + denylist | Revocable | Two storage concerns (JWT store + denylist); higher complexity than opaque approach | More complex than opaque token with no meaningful benefit |
| Opaque UUID (chosen) | Simple, auditable, native revocation, bulk revocation per user | Requires DB lookup on every token refresh | Accepted trade-off — refresh is infrequent (every 15 min at most) |

## Consequences

### Positive

- Immediate revocation on logout — set `revoked = true`, done.
- Bulk revocation on user deactivation — `UPDATE refresh_tokens SET revoked = true WHERE user_id = ?`
- Full audit trail — every issued token is a row; can inspect active sessions per user.
- No token signing key management for refresh tokens.

### Negative

- Every token refresh requires a DB read (`SELECT ... WHERE token = ?`). Mitigated by the unique index on `token`.
- Token rows accumulate; requires periodic cleanup of expired tokens (`deleteAllExpired()` via `@Scheduled`).

### Risks

- **Token leakage**: opaque token has no expiry information encoded in it — an attacker who obtains a token before it's revoked can use it until expiry. Mitigated by short transport window (HTTPS only) and the 7-day max lifetime.

## Related

- **Stories**: 002-user-login, 003-user-logout
- **Standards**: Establish as standard for all future session token decisions in StyleGenie
- **Previous ADRs**: None
