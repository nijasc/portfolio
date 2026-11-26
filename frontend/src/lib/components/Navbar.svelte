<script lang="ts">
    import icon from "#lib/assets/icon.svg"
    import {routes} from "#routes";
</script>

<nav class="fixed top-4 left-0 w-full z-100">
    <div class="mx-4 bg-base-200 rounded-box p-4 flex flex-row justify-between items-center">
        <a href={routes.home()} class="flex flex-row space-x-4 items-center">
            <img src={icon} alt="Rubik's cube" class="h-16">
            <span class="text-xl">Nijasc</span>
        </a>

        <ul class="flex flex-row space-x-8">
            <li><a class="nav-link" href={routes.blog()}>Blog</a></li>
            <li><a class="nav-link" href={routes.projects()}>Projects</a></li>
            <li><a class="nav-link" href={routes.discussion()}>Discussion</a></li>
        </ul>
        <div>
            <a class="btn btn-primary" href={routes.login()}>Login</a>
        </div>
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
