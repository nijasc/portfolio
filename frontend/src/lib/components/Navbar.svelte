<script lang="ts">
    import icon from '#lib/assets/icon.svg';
    import { routes } from '#routes';
    import { afterNavigate, goto } from '$app/navigation';
    import { cubicOut } from 'svelte/easing';
    import GitHubIcon from '#lib/custom-icons/GitHubIcon.svelte';
    import { m } from '#i18n';
    import { links } from '#lib';

    const MIN_WIDTH = 6;
    const MIN_HEIGHT = 6;
    const MAX_HEIGHT = 21.5;
    const MAX_WIDTH_MOBILE = 22;
    const MAX_WIDTH_DESKTOP = 64;
    const ANIMATION_DURATION = 300;
    const TAILWIND_SIZE_MD = 768;

    let mounted = $state(false);
    let pageWidth = $state(TAILWIND_SIZE_MD + 1);
    const isMobile = $derived(pageWidth <= TAILWIND_SIZE_MD);
    const maxWidth = $derived(isMobile ? MAX_WIDTH_MOBILE : MAX_WIDTH_DESKTOP);
    const hidingThreshold = $derived(maxWidth / 2);

    let isOpen = $state(true);
    let isWidthCollapsed = $state(false);
    let navWidth = $state(MAX_WIDTH_DESKTOP);
    let navHeight = $state(MIN_HEIGHT);
    const navStyles = $derived(mounted ? `width: ${navWidth}rem; height: ${navHeight}rem;` : undefined);

    let oldScrollY = 0;
    let frame: number | undefined;

    function targetWidth(): number {
        return isOpen ? maxWidth : MIN_WIDTH;
    }

    function targetHeight(): number {
        return isOpen && isMobile ? MAX_HEIGHT : MIN_HEIGHT;
    }

    function cancelAnimation(): void {
        if (frame !== undefined) {
            cancelAnimationFrame(frame);
            frame = undefined;
        }
    }

    function animate(): void {
        cancelAnimation();
        const startWidth = navWidth;
        const startHeight = navHeight;
        const endWidth = targetWidth();
        const endHeight = targetHeight();
        const startTime = performance.now();
        isWidthCollapsed = false;

        const step = (now: number): void => {
            const progress = Math.min((now - startTime) / ANIMATION_DURATION, 1);
            const eased = cubicOut(progress);
            navWidth = startWidth + (endWidth - startWidth) * eased;
            navHeight = startHeight + (endHeight - startHeight) * eased;

            if (progress < 1) {
                frame = requestAnimationFrame(step);
                return;
            }

            frame = undefined;
            isWidthCollapsed = !isOpen;
        };

        frame = requestAnimationFrame(step);
    }

    function jumpToTarget(): void {
        cancelAnimation();
        navWidth = targetWidth();
        navHeight = targetHeight();
        isWidthCollapsed = !isOpen;
    }

    function open(): void {
        if (isOpen) return;
        isOpen = true;
        animate();
    }

    function close(): void {
        if (!isOpen) return;
        isOpen = false;
        animate();
    }

    function onScroll(): void {
        const newScrollY = window.scrollY;

        if (isMobile || newScrollY > oldScrollY) {
            close();
        } else if (newScrollY < oldScrollY) {
            open();
        }

        oldScrollY = newScrollY;
    }

    function onResize(): void {
        const wasMobile = isMobile;
        pageWidth = window.innerWidth;
        if (wasMobile === isMobile) return;
        isOpen = !isMobile;
        animate();
    }

    function iconPressed(): void {
        if (!isOpen) {
            open();
        } else if (isMobile) {
            close();
        } else {
            void goto(routes.home());
        }
    }

    afterNavigate(({ type }) => {
        if (type === 'enter') {
            pageWidth = window.innerWidth;
            oldScrollY = window.scrollY;
            isOpen = !isMobile;
            jumpToTarget();
            mounted = true;
        } else if (isMobile) {
            close();
        }
    });

    $effect(() => () => cancelAnimation());
</script>

<svelte:window onresize={onResize} onscroll={onScroll} />
<nav
    class="fixed md:top-4 md:left-0 right-0 md:bottom-auto bottom-12 w-full z-100 px-4 flex justify-end md:justify-center"
>
    <div
        class={[
            'overflow-hidden space-y-2 md:space-y-0 shadow shadow-base-300 bg-base-200 rounded-box p-4 flex flex-col-reverse md:flex-row md:justify-between items-center',
            !mounted && 'w-24 h-24 md:w-5xl'
        ]}
        style={navStyles}
    >
        <div class="w-full md:w-fit flex flex-row p-0 m-0">
            {#if isMobile && !isWidthCollapsed}
                <div class="flex flex-row items-center md:hidden w-full">
                    {#if navWidth > hidingThreshold + hidingThreshold / 2}
                        <a target="_blank" class="btn md:hidden btn-neutral w-fit" href={links.githubRepo}>
                            <GitHubIcon />
                        </a>
                        <span class="text-base-content/50 text-sm w-full text-center">
                            Nijasc &copy; {new Date().getFullYear()}
                        </span>
                    {/if}
                </div>
            {/if}
            <button
                onclick={iconPressed}
                class="flex select-none cursor-pointer w-fit flex-row-reverse md:flex-row space-x-6 items-center"
            >
                <img src={icon} alt="Rubik's cube" class="h-16 w-16 min-w-16" />
                {#if navWidth > hidingThreshold && !isMobile}
                    <span class={['text-xl', !mounted && 'max-md:hidden']}>Nijasc</span>
                {/if}
            </button>
        </div>
        {#if navWidth > hidingThreshold}
            <ul
                class={[
                    'flex flex-col-reverse space-y-2 mt-2 md:mt-0 md:space-y-0 md:flex-row md:space-x-8 w-full md:w-fit text-right',
                    !mounted && 'max-md:hidden'
                ]}
            >
                <li class="nav-link md:hidden"><a href={routes.home()}>{m.home()}</a></li>
                <li class="nav-link"><a href={routes.blog()}>{m.blog()}</a></li>
                <li class="nav-link"><a href={routes.projects()}>{m.projects()}</a></li>
                <li class="nav-link"><a href={routes.discussion()}>{m.discussion()}</a></li>
            </ul>
            <ul
                class={[
                    'flex flex-col space-y-2 md:space-y-0 md:flex-row md:space-x-2 items-center w-full md:w-fit',
                    !mounted && 'max-md:hidden'
                ]}
            >
                <li class="w-full md:w-fit">
                    <a target="_blank" class="btn hidden md:flex btn-neutral w-full md:w-fit" href={links.githubRepo}>
                        <GitHubIcon />
                    </a>
                </li>
                <li class="w-full md:w-fit">
                    <a class="btn btn-primary w-full md:w-fit md:justify-center justify-end" href={routes.login()}>
                        {m.login()}
                    </a>
                </li>
            </ul>
        {/if}
    </div>
</nav>

<style lang="postcss">
    @reference "../ktor-svelte/app.css";

    .nav-link {
        @apply md:bg-clip-text md:bg-base-content md:text-transparent w-full md:w-fit border md:border-none border-base-300 rounded-box pr-2 py-2;
    }

    .nav-link:hover {
        @apply md:from-info md:to-base-content md:bg-linear-to-bl;
    }
</style>
