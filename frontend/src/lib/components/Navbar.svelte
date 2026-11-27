<script lang="ts">
    import icon from '#lib/assets/icon.svg';
    import { routes } from '#routes';
    import { afterNavigate, goto } from '$app/navigation';
    import { links } from '#lib';
    import GitHubIcon from '#lib/custom-icons/GitHubIcon.svelte';

    const MIN_WIDTH = 6;
    const MIN_HEIGHT = 6;
    const MAX_WIDTH = 64;
    const MAX_HEIGHT = 15;
    const HIDING_THRESHOLD = 30;
    const ANIMATION_HEIGHT_DELAY = 10;
    const ANIMATION_WIDTH_DELAY = 8;
    const TAILWIND_SIZE_MD = 768;

    let navBar: HTMLDivElement | null = $state(null);
    let navHeight = $state(MIN_HEIGHT);
    let pageWidth = $state(0);
    let isMobile = $derived(pageWidth <= TAILWIND_SIZE_MD);
    let navWidth = $state(MAX_WIDTH);
    let navStyles = $derived('width: ' + navWidth + 'rem; height: ' + navHeight + 'rem;');
    let oldScrollY = $state(0);
    let isOpen = $state(true);
    let isChanging = $state(false);
    let isWidthCollapsed = $state(false);

    function updateScroll() {
        let newScrollY = window.scrollY;

        if (isMobile) {
            close();
        } else if (newScrollY > oldScrollY) {
            close();
        } else if (newScrollY < oldScrollY) {
            open();
        }
        oldScrollY = newScrollY;
    }

    function iconPressed() {
        if (!isOpen && isWidthCollapsed && isMobile) {
            open(true);
        } else if (!isOpen && isWidthCollapsed) {
            open();
        } else if (isMobile && isOpen) {
            close();
        } else if (isOpen) {
            goto(routes.home());
        }
    }

    function open(withHeight: boolean = false) {
        if (isChanging) return;
        isChanging = true;
        isOpen = true;
        expandWidth();
        if (withHeight) expandHeight();
        isChanging = false;
    }

    async function expandWidth() {
        isWidthCollapsed = false;
        while (navWidth < MAX_WIDTH && isOpen) {
            await sleep(ANIMATION_WIDTH_DELAY);
            navWidth++;
        }
    }

    async function expandHeight() {
        while (navHeight < MAX_HEIGHT && isOpen) {
            await sleep(ANIMATION_HEIGHT_DELAY);
            navHeight++;
        }
    }

    function close() {
        if (isChanging) return;
        isChanging = true;
        isOpen = false;
        shrinkWidth();
        shrinkHeight();
        isChanging = false;
    }

    async function shrinkHeight() {
        while (navHeight > MIN_HEIGHT && !isOpen) {
            await sleep(ANIMATION_HEIGHT_DELAY);
            navHeight--;
        }
    }

    async function shrinkWidth() {
        isWidthCollapsed = false;

        while (navWidth > MIN_WIDTH && !isOpen) {
            await sleep(ANIMATION_WIDTH_DELAY);
            navWidth--;
        }
        isWidthCollapsed = true;
    }

    async function sleep(ms: number): Promise<void> {
        return new Promise((resolve) => setTimeout(resolve, ms));
    }

    async function onResize() {
        let windowSize = window.outerWidth;
        let mobile = windowSize <= TAILWIND_SIZE_MD;
        if (mobile) {
            open(true);
            initMobileSizes();
        } else {
            open();
        }
    }

    function initMobileSizes() {
        setTimeout(() => {
            close();
        }, 300);
    }

    afterNavigate(() => {
        if (isMobile) {
            initMobileSizes();
        }
    });

    $effect(() => {
        if (navWidth < MIN_WIDTH) navWidth = MIN_WIDTH;
        if (navHeight < MIN_HEIGHT) navHeight = MIN_HEIGHT;
    });
</script>

<svelte:window on:resize={onResize} on:scroll={updateScroll} bind:outerWidth={pageWidth} />
<nav
    class="fixed md:top-4 md:left-0 right-0 md:bottom-auto bottom-12 w-full z-100 px-4 flex justify-end md:justify-center"
>
    <div
        bind:this={navBar}
        class="overflow-hidden space-y-2 md:space-y-0 shadow shadow-base-300 bg-base-200 rounded-box p-4 flex flex-col-reverse md:flex-row md:justify-between items-center"
        style={navStyles}
    >
        <div class="w-full md:w-fit flex flex-row p-0 m-0">
            {#if isMobile && !isWidthCollapsed}
                <div class="flex md:hidden w-full"></div>
            {/if}
            <button
                onclick={iconPressed}
                class="flex select-none cursor-pointer w-fit flex-row-reverse md:flex-row space-x-6 items-center"
            >
                <img src={icon} alt="Rubik's cube" class="h-16 w-16 min-w-16" />
                {#if navWidth > HIDING_THRESHOLD && !isMobile}
                    <span class="text-xl">Nijasc</span>
                {/if}
            </button>
        </div>
        {#if navWidth > HIDING_THRESHOLD}
            <ul class="flex flex-col space-y-2 md:space-y-0 md:flex-row md:space-x-8 w-full md:w-fit text-right">
                <li><a class="nav-link" href={routes.blog()}>Blog</a></li>
                <li><a class="nav-link" href={routes.projects()}>Projects</a></li>
                <li><a class="nav-link" href={routes.discussion()}>Discussion</a></li>
            </ul>
            <ul class="flex flex-col space-y-2 md:space-y-0 md:flex-row md:space-x-2 items-center w-full md:w-fit">
                <li class="w-full md:w-fit">
                    <a target="_blank" class="btn hidden md:flex btn-neutral w-full md:w-fit" href={links.githubRepo}>
                        <GitHubIcon />
                    </a>
                </li>
                <li class="w-full md:w-fit">
                    <a class="btn btn-primary w-full md:w-fit" href={routes.login()}>Login</a>
                </li>
            </ul>
        {/if}
    </div>
</nav>

<style lang="postcss">
    @reference "../ktor-svelte/app.css";

    .nav-link {
        @apply bg-clip-text bg-base-content text-transparent w-full md:w-fit;
    }

    .nav-link:hover {
        @apply from-info to-base-content bg-linear-to-bl;
    }
</style>
