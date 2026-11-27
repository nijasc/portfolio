import { defineRailway, github, postgres, preserve, project, service } from 'railway/iac';

export default defineRailway(() => {
    const db = postgres('postgres', {
        region: "europe-west4-drams3a"
    });
    const app = service('nijasc-portfolio', {
        source: github('nijasc/nijasc-portfolio', { branch: 'main' }),
        build: './gradlew buildFatJar --no-daemon',
        start: 'java -jar build/libs/nijasc-portfolio-all.jar',
        healthcheck: '/_ktor-svelte/health',
        healthcheckTimeout: 60,
        regions: {"europe-west4-drams3a": 1},
        env: {
            DATABASE_URL: `jdbc:postgresql://${db.env.PGHOST}:${db.env.PGPORT}/${db.env.PGDATABASE}`,
            POSTGRES_USER: db.env.PGUSER,
            POSTGRES_DB: db.env.PGDATABASE,
            POSTGRES_PASSWORD: db.env.PGPASSWORD,
            SESSION_SECRET: preserve(),
            COOKIE_SECURE: 'true',
        },
    });

    return project('nijasc-portfolio', {
        resources: [db, app],
    });
});
