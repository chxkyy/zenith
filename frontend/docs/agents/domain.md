# Domain Docs Layout

This repo uses a **single-context** layout.

## Structure

```
├── CONTEXT.md          # Project domain language, key concepts, terminology
└── docs/
    └── adr/            # Architecture Decision Records (ADRs)
        └── *.md
```

## Consumer Rules

- Skills that need domain context should read `CONTEXT.md` at the **repo root**
- ADRs live under `docs/adr/` at the repo root
- If `CONTEXT.md` does not yet exist, the skill should note its absence but not fail
- When suggesting new ADRs, place them in `docs/adr/` with naming convention `NNN-short-title.md`

## Notes

This is a frontend project (React/Vite). If you add domain documentation later, ensure it covers:
- Frontend-specific patterns and conventions
- Component architecture decisions
- API integration patterns
