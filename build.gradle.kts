import io.github.nijasc.ktor.svelte.gradle.DaisyTheme
import io.github.nijasc.ktor.svelte.gradle.LocaleStrategy
import org.jetbrains.gradle.ext.packagePrefix
import org.jetbrains.gradle.ext.settings

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.ktor)
    application
    idea
    alias(libs.plugins.idea.ext)
    alias(libs.plugins.ktor.svelte)
}

group = "dev.nilsschlegel"
version = file("version.txt").readText().trim()

application {
    mainClass = "dev.nilsschlegel.ApplicationKt"
}

ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}

idea {
    module {
        settings {
            packagePrefix["src/main/kotlin"] = "dev.nilsschlegel"
        }
    }
}

tasks.shadowJar {
    mergeServiceFiles()
}

svelte {
    dev {
        openBrowser = false
        https = true
        httpsPort = 8081
    }
    libraries {
        tailwind {
            typography()
        }
        daisyUi {
            themes(DaisyTheme.ShadcnLight)
            defaultTheme(DaisyTheme.ShadcnLight)
        }
        lucide {
            allIcons()
        }
        i18n {
            locales("en", "de")
            defaultLocale = "en"
            strategy = LocaleStrategy.Cookie
        }
        routes()
    }
    utils {
        flyway()
    }
}

dependencies {
    implementation(projects.blogAdapters)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.sessions)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.hikari)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.postgresql)
    implementation(libs.logback)
}
