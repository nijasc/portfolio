# nijasc-portfolio

A Ktor backend and a SvelteKit frontend, served from one JAR on one port by [ktor-svelte](https://nijasc.github.io/ktor-svelte). The Kotlin package is `dev.nilsschlegel`, and the frontend uses bun.

Read `frontend/src/lib/ktor-svelte/AGENTS.md` before frontend work. It is generated, always current, and lists the helpers and components this project has.

## Commands

Run them from the project root. On Windows, use `gradlew.bat` instead of `./gradlew`.

| Task | Command |
|---|---|
| Run everything for development, then open http://localhost:8080 | `./gradlew svelteDevAll` |
| Start Postgres first | `docker compose up -d` |
| Kotlin tests and ktlint | `./gradlew check` |
| Frontend type check | `./gradlew svelteCheck` |
| Frontend lint | `./gradlew svelteRun --script=lint` |
| Format everything | `./gradlew ktlintFormat` and `./gradlew svelteRun --script=format` |
| Regenerate the TypeScript client after changing a route or a DTO | `./gradlew svelteGenerateTs` |
| Regenerate frontend files after changing `svelte { }` in `build.gradle.kts` | `./gradlew svelteSync` |
| Write the next migration after changing a table | `./gradlew generateMigrations` |
| Build the JAR | `./gradlew buildFatJar` |
| Build the container image | `docker build -t nijasc-portfolio .` |
| Find out why something does not work | `./gradlew svelteDoctor` |

Add frontend packages with `bun` in `frontend/`. Gradle runs the package manager for every other task.

## Where things are

- `src/main/kotlin/Application.kt`: starts Ktor, installs ktor-svelte and registers the routes.
- `src/main/kotlin/Env.kt`: `env()` and `requireEnv()` read the environment, then `.env`.
- `src/main/kotlin/Errors.kt`: turns exceptions into JSON problems. Throw `ApiException(status, message)` for an error the user should see.
- `src/main/kotlin/Auth.kt`: sessions, register, login and logout. Wrap protected routes in `authenticate("session")`.
- `src/main/kotlin/Database.kt`: the connection pool and Exposed, with Flyway migrations in `src/main/resources/db/migration`.
- Bounded contexts: `blog`, each in its own folder. See Architecture below.
- `frontend/src/routes`: the pages. `frontend/src/lib`: your own frontend code.
- `frontend/messages/<locale>.json`: the texts. Use `m.key()` in Svelte and `call.messages` in Kotlin.
- `build.gradle.kts`: dependencies and the `svelte { }` block, which configures frontend libraries. `gradle/libs.versions.toml`: every version.
- `.env.example`: every setting the app reads. `.env` holds local secrets and is never committed or printed.

## How the backend and frontend fit together

- API routes live under `/api`. Every other path is the SvelteKit app, built as static files.
- The frontend calls the API only through the generated client: `import { api } from '#api'`, then `await api.notes.list()`. Do not call `fetch` for `/api` by hand.
- Request and response bodies are `@Serializable` Kotlin classes. Their types, `empty()`, `bounds` and `validate()` appear in `#api` after `svelteGenerateTs`, which also runs when the app restarts in development. Commit the generated files.
- Put limits on DTO properties with `@JsonSchema` annotations such as `@JsonSchema.MaxLength(200)`, and extra rules with `Validated`. The server and the browser then check the same rules.
- Keep business rules in Kotlin. Svelte components show data and collect input.

## Architecture

The backend is split into bounded contexts, each a hexagon:

- `domain`: aggregates and their rules. No Ktor, no Exposed, no serialization.
- `application`: use cases and the ports they need, such as repository interfaces.
- `adapters`: `web` for Ktor routes and DTOs, `persistence` for repositories.
- The root project only wires adapters into use cases in `Application.kt`.

Each layer is its own Gradle module, so a wrong import does not compile. Never let a context use another context's adapters or tables. Route modules apply `ktor-routes-conventions`, so their DTOs reach the TypeScript client. To add a context, copy an existing one and register it in `settings.gradle.kts` and the root `build.gradle.kts`.

## Database

Tables change only through migrations. Never edit a migration that has run. After changing an Exposed table, run `./gradlew generateMigrations` and review the new file.

For dates and times, use the columns from `org.jetbrains.exposed.v1.datetime`: `timestamp` for a moment as `kotlin.time.Instant`, `date` for a `kotlinx.datetime.LocalDate`, `datetime` for a `LocalDateTime`. Use the same types in DTOs, and the TypeScript client gets `IsoDateTime` and `IsoDate`.

## Deployment

The app runs on Railway and deploys on each release that release-please creates through `.github/workflows/deploy.yml`. The infrastructure is code in `.railway/railway.ts`: change services, databases and variables there, never in the Railway dashboard. A pull request that touches `.railway/` gets a plan comment, and merging applies it. Run `railway config plan` locally to preview a change. Secrets stay in Railway: `preserve()` keeps their values, so never write a secret into the file.

## Files you must not edit

- `frontend/src/lib/api/`: the generated client. Change the Kotlin side and regenerate.
- `frontend/src/lib/ktor-svelte/`, `frontend/vite.config.ts` and any file that starts with "Generated by ktor-svelte". Change `build.gradle.kts` and run `./gradlew svelteSync`. Your own Vite settings go in `frontend/vite.user.ts`.
- `version.txt`, `.release-please-manifest.json` and `CHANGELOG.md`: release-please owns them.

## Conventions

- Kotlin follows ktlint's official style with lines up to 140 characters. The frontend uses Prettier and ESLint.
- Keep versions in `gradle/libs.versions.toml` and reference them as `libs.*`.
- Read a new setting with `env()` or `requireEnv()`, and list it in `.env.example` and the README in the same change.
- Commit messages follow Conventional Commits: `type(scope): summary` with `feat`, `fix`, `docs`, `test`, `build`, `ci`, `refactor` or `chore`. `feat` and `fix` become release notes, so describe what users notice.
- Dependabot pull requests that change a library ktor-svelte configures need `./gradlew svelteSync` and a commit of the generated files.
- CI runs `./gradlew check buildFatJar svelteCheck` and the frontend lint on every push, so run them before you finish.

## Before you finish

1. `./gradlew check svelteCheck` passes.
2. `./gradlew svelteRun --script=lint` passes. If it does not, run the format commands first.
3. If a route or DTO changed, `./gradlew svelteGenerateTs` ran and its output is part of the change.
4. The README and `.env.example` describe any new command or setting.

## Learn more

The ktor-svelte docs are at https://nijasc.github.io/ktor-svelte. `./gradlew svelteDoctor` checks the setup and links to the right page.
