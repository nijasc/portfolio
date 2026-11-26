plugins {
    id("ktor-routes-conventions")
}

dependencies {
    api(projects.blogApplication)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.svelte.server)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
}
