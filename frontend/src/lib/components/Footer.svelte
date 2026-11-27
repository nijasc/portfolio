<script lang="ts">
    import Icon from '#icon';
    import { icons } from '#lib/ktor-svelte/icons.ts';
    import { m } from '#i18n';
    import { project } from '#lib';
</script>

<div class="w-full p-4 pb-12 md:pb-8 flex justify-center">
    <div class="w-full md:w-7xl p-4 bg-base-200 rounded-box h-fit flex flex-row space-x-4 shadow-lg shadow-base-300">
        <div class="flex flex-col justify-between">
            <div>
                <Icon size={128} name="Cheese" />
            </div>
            <div class="flex flex-col space-y-2 text-base-content/70">
                <span class="footer-title">{m.contact()}</span>
                <span>{project.author}</span>
                <a class="footer-link" href="tel:{project.authorPhone}">tel: {project.authorPhone}</a>
                <a class="footer-link" href="mailto:{project.authorEmail}">mail: {project.authorEmail}</a>
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
