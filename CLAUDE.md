# Claude Code — Project Context

## Obsidian Vault

All project state, decisions, phase checklists, and daily logs live here:

```
/Users/a1/Gabriel/Obsidian Vault/Projects_Revamp/
```

**Read these files at the start of every session (in order):**

1. `00-Overview.md` — what the project is, tech stack, phase map
2. `05-Progress/Kanban.md` — what is done, in progress, and todo
3. `05-Progress/Daily-Log.md` — what was last worked on and why
4. The relevant phase file under `02-Backend/` or `03-Frontend/` for the current phase

The Kanban and Daily-Log are the source of truth for current state. Do not assume anything about what is or isn't done without reading them first.

## Repo Structure

```
rental-api/
├── rentacar-api/        # Spring Boot API (Maven project root)
│   ├── src/
│   ├── docs/            # Postman/Apidog collection
│   └── pom.xml
└── README.md
```

## Key Rules

- Never modify `mvnw` or `mvnw.cmd` — generated Maven wrapper scripts
- `application-dev.properties` and `application-prod.properties` are gitignored — never commit them
- Dead code = delete it, not suppress with `@SuppressWarnings("unused")`
- `@SuppressWarnings("unused")` is only for Spring framework false positives (injection, SpEL, `@Bean` lifecycle)
- No co-author attribution in git commit messages
