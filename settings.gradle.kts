pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "nijasc-portfolio"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

fun boundedContext(name: String) {
    listOf("domain", "application", "adapters").forEach { layer ->
        include("$name-$layer")
        project(":$name-$layer").projectDir = file("$name/$layer")
    }
}

include("shared-kernel")
boundedContext("blog")
