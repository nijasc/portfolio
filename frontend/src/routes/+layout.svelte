<script lang="ts">
    import '#lib/ktor-svelte/app.css';
    import Navbar from '#lib/components/Navbar.svelte';
    import Footer from '#lib/components/Footer.svelte';
    import { m } from '#i18n';
    import { project } from '#lib';

    let { children } = $props();
</script>

<svelte:head>
    <title>{project.author} - {m.portfolio()}</title>
</svelte:head>

<Navbar />
{@render children()}
<Footer />
