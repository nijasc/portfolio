import { defineRailway, github, postgres, preserve, project, service } from 'railway/iac';

export default defineRailway(() => {
    const db = postgres('postgres');
    const app = service('nijasc-portfolio', {
        source: github('nijasc/nijasc-portfolio', { branch: 'main' }),
        build: './gradlew buildFatJar --no-daemon',
        start: 'java -jar build/libs/nijasc-portfolio-all.jar',
        healthcheck: '/_ktor-svelte/health',
        healthcheckTimeout: 60,
        env: {
            DATABASE_URL: db.env.DATABASE_URL,
            SESSION_SECRET: preserve(),
            COOKIE_SECURE: 'true',
        },
    });

    return project('nijasc-portfolio', {
        resources: [db, app],
    });
});
