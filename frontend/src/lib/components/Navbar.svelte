<script lang="ts">
    import icon from '#lib/assets/icon.svg';
    import { routes } from '#routes';
    import { goto } from '$app/navigation';
    import { links } from '#lib';
    import GitHubIcon from '#lib/custom-icons/GitHubIcon.svelte';

    const MIN_WIDTH = 6;
    const MAX_WIDTH = 64;
    const HIDING_THRESHOLD = 30;
    const ANIMATION_DELAY_MS = 2;

    let navWidth = $state(MAX_WIDTH);
    let navStyles = $derived('width: ' + navWidth + 'rem;');
    let oldScrollY = $state(0);
    let isOpen = $state(true);
    let isChanging = $state(false);

    function updateScroll() {
        let newScrollY = window.scrollY;

        if (newScrollY > oldScrollY) {
            close();
        } else if (newScrollY < oldScrollY) {
            open();
        }
        oldScrollY = newScrollY;
    }

    function iconPressed() {
        if (!isOpen) {
            open();
        } else {
            goto(routes.home());
        }
    }

    async function open() {
        if (isChanging) return;
        isChanging = true;
        isOpen = true;
        while (navWidth < MAX_WIDTH && isOpen) {
            await sleep();
            navWidth++;
        }
        isChanging = false;
    }

    async function close() {
        if (isChanging) return;
        isChanging = true;
        isOpen = false;
        while (navWidth > MIN_WIDTH && !isOpen) {
            await sleep();
            navWidth--;
        }
        isChanging = false;
    }

    async function sleep(): Promise<void> {
        return new Promise((resolve) => setTimeout(resolve, ANIMATION_DELAY_MS));
    }
</script>

<svelte:window on:scroll={updateScroll} />

<nav class="fixed top-4 left-0 w-full z-100 px-4 flex justify-center">
    <div
        class="overflow-x-hidden shadow shadow-base-300 bg-base-200 rounded-box p-4 flex flex-row justify-between items-center"
        style={navStyles}
    >
        <button onclick={iconPressed} class="flex select-none cursor-pointer flex-row space-x-6 items-center">
            <img src={icon} alt="Rubik's cube" class="h-16" />
            {#if navWidth > HIDING_THRESHOLD}
                <span class="text-xl">Nijasc</span>
            {/if}
        </button>
        {#if navWidth > HIDING_THRESHOLD}
            <ul class="flex flex-row space-x-8">
                <li><a class="nav-link" href={routes.blog()}>Blog</a></li>
                <li><a class="nav-link" href={routes.projects()}>Projects</a></li>
                <li><a class="nav-link" href={routes.discussion()}>Discussion</a></li>
            </ul>
            <div class="flex flex-row space-x-2 items-center">
                <a target="_blank" class="btn btn-neutral" href={links.githubRepo}>
                    <GitHubIcon />
                </a>
                <a class="btn btn-primary" href={routes.login()}>Login</a>
            </div>
        {/if}
    </div>
</nav>

<style>
    @reference "../ktor-svelte/app.css";

    .nav-link {
        @apply bg-clip-text bg-base-content text-transparent;
    }

    .nav-link:hover {
        @apply from-info to-base-content bg-linear-to-bl;
    }
</style>
