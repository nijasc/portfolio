import { defineRailway, postgres, preserve, project, service } from 'railway/iac';

export default defineRailway(() => {
    const db = postgres('postgres');
    const app = service('nijasc-portfolio', {
        healthcheck: '/_ktor-svelte/health',
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
