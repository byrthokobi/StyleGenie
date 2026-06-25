---
intent: 001-ai-outfit-recommendation
phase: inception
status: draft
created: 2026-06-25T00:00:00Z
---

# Units: 001-ai-outfit-recommendation

## Project Type: backend-api (backend only — no UI unit in scope)
Backend: domain-driven (ddd-construction-bolt)

---

## Requirement-to-Unit Mapping

| FR | Requirement | Unit |
|----|-------------|------|
| FR-1 | User Registration & Authentication | `001-auth-service` |
| FR-2 | Style Profile Setup | `002-profile-service` |
| FR-3 | User Profile Photo Upload | `002-profile-service` |
| FR-4 | Wardrobe Management | `003-wardrobe-service` |
| FR-5 | Occasion Selection | `004-recommendation-service` |
| FR-6 | AI Outfit Recommendation (Spring AI) | `004-recommendation-service` |
| FR-7 | Admin Panel | `005-admin-service` |

---

## Units

### Unit 001: auth-service (Backend)
- **Purpose**: User registration, authentication, and JWT-based session management
- **Responsibility**: Identity and access — register, login, logout, token lifecycle, RBAC
- **Assigned Requirements**: FR-1
- **Dependencies**: None
- **Depended By**: All other backend units
- **Bolt Type**: ddd-construction-bolt

### Unit 002: profile-service (Backend)
- **Purpose**: User style profile management and profile photo storage
- **Responsibility**: Store and retrieve body shape, face shape, skin tone selections and profile photo
- **Assigned Requirements**: FR-2, FR-3
- **Dependencies**: `001-auth-service`
- **Depended By**: `004-recommendation-service`
- **Bolt Type**: ddd-construction-bolt

### Unit 003: wardrobe-service (Backend)
- **Purpose**: Digital wardrobe — CRUD for clothing items with photo upload
- **Responsibility**: Add, edit, delete, and list wardrobe items with category, colour, and photo metadata
- **Assigned Requirements**: FR-4
- **Dependencies**: `001-auth-service`, `002-profile-service`
- **Depended By**: `004-recommendation-service`
- **Bolt Type**: ddd-construction-bolt

### Unit 004: recommendation-service (Backend)
- **Purpose**: Outfit recommendation orchestration via Spring AI
- **Responsibility**: Accept occasion input, build a prompt from user traits + wardrobe + occasion, call Spring AI ChatClient, return outfit combinations
- **Assigned Requirements**: FR-5, FR-6
- **Dependencies**: `002-profile-service`, `003-wardrobe-service`
- **Depended By**: None (leaf service)
- **Bolt Type**: ddd-construction-bolt

### Unit 005: admin-service (Backend)
- **Purpose**: Admin panel backend — user management and platform activity monitoring
- **Responsibility**: Admin-only endpoints for listing, editing, and deactivating users; viewing activity logs
- **Assigned Requirements**: FR-7
- **Dependencies**: `001-auth-service`
- **Depended By**: None
- **Bolt Type**: ddd-construction-bolt

---

## Dependency Graph

```
001-auth-service ──────────────────────────────► 002-profile-service
       │                                                  │
       │                                                  ▼
       │                                         003-wardrobe-service
       │                                                  │
       │                                    ──────────────┘
       │                                   ▼
       └──────────────────────────► 004-recommendation-service (Spring AI internal)
       │
       └──────────────────────────► 005-admin-service
```

---

## Summary

| Unit | Type | FR Coverage | Bolts (planned) |
|------|------|-------------|-----------------|
| 001-auth-service | backend | FR-1 | 2 |
| 002-profile-service | backend | FR-2, FR-3 | 1 |
| 003-wardrobe-service | backend | FR-4 | 1 |
| 004-recommendation-service | backend | FR-5, FR-6 | 1 |
| 005-admin-service | backend | FR-7 | 1 |
| **Total** | | 7 FRs | **6 bolts** |
