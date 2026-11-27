<script lang="ts">
    import {getLocale, locales, m, setLocale} from '#i18n';
    import {project} from '#lib';
    import Icon from "#icon";
    import {icons} from "#lib/ktor-svelte/icons.ts";
    import {getTheme, setTheme, themes} from "#theme";
    import {routes} from "#routes";

    let lang = $state(getLocale())
    let theme = $state(getTheme())

    $effect(() => {
        if (getLocale() != lang) setLocale(lang)
        if (getTheme() != theme) setTheme(theme)
    })
</script>

<div class="w-full p-4 pb-12 md:pb-8 flex justify-center">
    <div class="w-full md:w-7xl bg-base-200 rounded-box h-fit flex flex-col shadow-lg shadow-base-300">
        <div class="bg-primary font-bold text-primary-content select-none p-4 rounded-t-box">
            <span class="font-mono text-[12rem]">{project.name}</span>
        </div>
        <div class="flex flex-row justify-between px-8 py-4">
            <div class="flex flex-col space-y-2 text-base-content/70">
                <span class="footer-title">{m.contact()}</span>
                <span>{project.author}</span>
                <a class="footer-link" href="tel:{project.authorPhone}">tel: {project.authorPhone}</a>
                <a class="footer-link" href="mailto:{project.authorEmail}">mail: {project.authorEmail}</a>
            </div>
            <div class="flex flex-col space-y-2 text-base-content/70">
                <span class="footer-title">{m.pages()}</span>
                <a class="footer-link" href="{routes.blog()}">{m.blog()}</a>
                <a class="footer-link" href="{routes.projects()}">{m.projects()}</a>
                <a class="footer-link" href="{routes.discussion()}">{m.discussion()}</a>
                <a class="footer-link" href="{routes.login()}">{m.login()}</a>
            </div>
            <div class="flex flex-col space-y-2 text-base-content/70">
                <span class="footer-title">{m.resources()}</span>
                <a class="footer-link" href="#">{m.privacyPolicy()}</a>
                <a class="footer-link" href="#">{m.termsOfService()}</a>
                <a class="footer-link" href="#">{m.manageCookies()}</a>
            </div>
            <div class="flex flex-col space-y-2 justify-center text-base-content/70">
                <label class="select w-48">
                    <span class="label"><Icon size={18} name={icons.Languages!!.name} /></span>
                    <select bind:value={lang}>
                        {#each Object.values(locales) as locale}
                            <option value={locale}>{locale}</option>
                        {/each}
                    </select>
                </label>
                <label class="select w-48">
                    <span class="label"><Icon size={18} name={icons.Paintbrush!!.name} /></span>
                    <select bind:value={theme}>
                        {#each Object.values(themes) as theme}
                            <option value={theme}>{theme}</option>
                        {/each}
                    </select>
                </label>
            </div>
        </div>
    </div>
</div>

<style lang="postcss">
    @reference "../ktor-svelte/app.css"

    .footer-title {
        @apply uppercase;
    }

    .footer-link {
    }
</style>
