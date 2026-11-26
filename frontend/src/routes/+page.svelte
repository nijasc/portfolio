<script lang="ts">
    import Icon from '#icon';
    import { getLocale, localizeHref, m } from '#i18n';

    const docs = 'https://nijasc.github.io/ktor-svelte';
</script>

<main class="mx-auto max-w-2xl p-8">
    <h1 class="flex items-center gap-2 text-3xl font-bold">
        <Icon name="Rocket" />
        {m.welcome({ language: getLocale() })}
    </h1>
    <p>Edit <code>frontend/src/routes/+page.svelte</code>. Your API is at <code>/api/hello</code>.</p>
    <a class="btn btn-primary mt-4" href={docs}>Read the docs</a>
    <a class="btn mt-4" href={localizeHref('/login')}>Sign in</a>
    <h2 class="mt-8 text-xl font-semibold">What is set up</h2>
    <ul class="mt-2 list-disc pl-6">
        <li><code>/api/hello</code> answers from Kotlin, in <code>Application.kt</code></li>
        <li><a class="link" href="{docs}/features/typescript">A typed client</a> in <code>src/lib/api</code></li>
        <li><a class="link" href="{docs}/features/frontend-libraries#daisyui">DaisyUI</a>, themed from Gradle</li>
        <li><a class="link" href="{docs}/features/frontend-libraries#lucide">Lucide icons</a>, typed on both sides</li>
        <li><a class="link" href="{docs}/features/frontend-libraries#i18n">Messages</a> in <code>messages</code></li>
        <li><a class="link" href="{docs}/features/auth">Sign in</a> with sessions and hashed passwords</li>
        <li>Postgres with Exposed. Start it with <code>docker compose up -d</code></li>
        <li>
            The <code>blog</code> context, with <code>Blog</code> at <code>/api/blog</code>, split into domain,
            application and adapters
        </li>
    </ul>
    <h2 class="mt-8 text-xl font-semibold">What to do next</h2>
    <ul class="mt-2 list-disc pl-6">
        <li>Add a route in Kotlin and save: the app restarts and the typed client follows</li>
        <li>Click the API button in the corner of this page to see every call the app answers</li>
        <li><a class="link" href="{docs}/guide/tutorial">Follow the tutorial</a> to build a first feature</li>
        <li>Run <code>./gradlew svelteDoctor</code> when something looks off</li>
    </ul>
</main>
