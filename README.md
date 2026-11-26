# nijasc-portfolio

A Ktor app with a SvelteKit frontend, served from one JAR on one port by [ktor-svelte](https://github.com/nijasc/ktor-svelte).

## Develop

```bash
docker compose up -d
./gradlew svelteDevAll
```

Then open http://localhost:8080. In IntelliJ, run the `Dev` configuration. On Windows, use `gradlew.bat`.

## Check

```bash
./gradlew check svelteCheck
./gradlew svelteRun --script=lint
```

Format with `./gradlew ktlintFormat` and `./gradlew svelteRun --script=format`.

## Build

```bash
./gradlew buildFatJar
java -jar build/libs/nijasc-portfolio-all.jar
```

## Docker

```bash
docker build -t nijasc-portfolio .
docker run -p 8080:8080 --env-file .env nijasc-portfolio
```

Inside a container, `localhost` is the container itself, so point `DATABASE_URL` at your database host.

## Configuration

The app reads the environment first, then a `.env` file in the working directory. `.env.example` lists every setting, and `.env` is not committed.

| Setting | Purpose |
|---|---|
| `PORT` | HTTP port, 8080 by default |
| `SESSION_SECRET` | Signs session cookies, at least 32 characters. Without it, a new one is made at each start and everyone is signed out |
| `COOKIE_SECURE` | Send the session cookie over HTTPS only. Defaults to `true`, so set `false` only for local HTTP |
| `DATABASE_URL` | JDBC URL of the Postgres database, or a `postgresql://user:password@host:port/name` URL that carries the user and password too |
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | Used by `compose.yaml` and by the app |

## Sign in

`Auth.kt` has the register, login, logout and current user routes, with passwords hashed by PBKDF2. Wrap your own routes in `authenticate("session")`. Users are stored in Postgres. Registration is open and login has no rate limit, so restrict both before going live. The pages in `frontend/src/routes/login` and `frontend/src/routes/account` use `frontend/src/lib/auth.ts`.

## Database

`docker compose up -d` starts Postgres on this machine, using the settings in `.env`. `Database.kt` connects when the app starts and Flyway runs any new migration in `src/main/resources/db/migration`. Add a new file for each change and never edit one that has already run: after changing a table, `./gradlew generateMigrations` writes the next one for you. `POST /api/visits` shows the pattern with a small example table, so replace it with your own.

Exposed's date and time module is included: `timestamp`, `date` and `datetime` columns from `org.jetbrains.exposed.v1.datetime` read and write `kotlin.time.Instant` and kotlinx-datetime values, as the `at` column of the example table shows.

## Project layout

Kotlin files sit directly in `src/main/kotlin`, without package folders. The `idea-ext` plugin in `build.gradle.kts` sets a package prefix, so IntelliJ creates new files in the right package. Reload the Gradle project if IntelliJ does not pick it up.

`build-logic` holds the `kotlin-conventions` plugin with the Kotlin and ktlint setup. To add a module, add it to `settings.gradle.kts` and apply `id("kotlin-conventions")` in its build file.

## Architecture

The backend is split into bounded contexts, each built as a hexagon. The root project only starts the app and wires the contexts together in `Application.kt`.

| Context | Aggregate | API |
|---|---|---|
| `blog` | `Blog` | `/api/blog` |

Each context has three modules, and Gradle only lets each one see the layer below it:

- `<context>/domain`: the aggregate and its rules. No framework code.
- `<context>/application`: use cases, and the ports they need, such as the repository interface.
- `<context>/adapters`: the HTTP routes in `web` and the repository in `persistence`, which plug Ktor and Exposed into the ports.

To add a context, add `boundedContext("name")` to `settings.gradle.kts`, create the three folders with a `build.gradle.kts` like the existing ones, and depend on its adapters in the root `build.gradle.kts`.

`shared-kernel` holds code every context may use, such as `DomainException`, which the app answers with 422. Keep it small: everything in it couples the contexts.

Modules depend on each other through type-safe accessors such as `projects.blogDomain`, and on libraries through `gradle/libs.versions.toml`.

## Releases

[release-please](https://github.com/googleapis/release-please) reads the commit messages on `main` and keeps a release pull request open. Merging it raises the version in `version.txt`, which `build.gradle.kts` uses, writes `CHANGELOG.md` and creates the GitHub release. `feat` raises the minor version, `fix` the patch version, and a `!` after the type marks a breaking change. In the repository settings, under Actions, allow GitHub Actions to create pull requests. Never edit `version.txt` or `.release-please-manifest.json` by hand.

## Deploy to Railway

`.railway/railway.ts` describes the Railway project as code: the `nijasc-portfolio` service, built from the `Dockerfile`, with its health check, and a Postgres database whose `DATABASE_URL` the app reads. `.github/workflows/deploy.yml` deploys on each release that release-please creates, and `.github/workflows/railway-config.yml` plans changes to `.railway/` on pull requests and applies them when they merge.

Set it up once:

1. Install the [Railway CLI](https://docs.railway.com/cli), sign in with `railway login`, and create a project with `railway init`.
2. Apply the configuration: `npm install --prefix .railway`, then `railway config apply`.
3. Give the app a session secret: `railway variables --service nijasc-portfolio --set SESSION_SECRET=$(openssl rand -hex 32)`.
4. Create a project token for the production environment in the project settings, and save it as the `RAILWAY_TOKEN` secret of this GitHub repository.
5. Give the service a public address with `railway domain`.

After that, change the infrastructure only in `.railway/railway.ts`. Run the deploy workflow by hand from the Actions tab to deploy without waiting.

## Dependency updates

Dependabot opens one pull request a week for the Gradle dependencies, one for the frontend packages and, with GitHub Actions, one for the Actions. When an update changes a frontend library that ktor-svelte configures, such as Lucide or DaisyUI, the build asks you to run `./gradlew svelteSync`. Check out the pull request, run it, and commit the generated files.

## Commit messages

Commit messages follow [Conventional Commits](https://www.conventionalcommits.org), such as `feat(orders): show the total`. `.githooks/commit-msg` rejects any other message. After cloning, turn it on once:

```bash
git config core.hooksPath .githooks
```

## License

MIT, see [LICENSE](LICENSE).
