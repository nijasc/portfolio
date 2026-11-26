plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.jvm")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val ktorCompilerPlugin = "plugin:io.ktor.ktor-compiler-plugin"

dependencies {
    kotlinCompilerPluginClasspathMain(libs.findLibrary("ktor-compiler-plugin").get())
    implementation(libs.findLibrary("ktor-server-routing-openapi").get())
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-P",
            "$ktorCompilerPlugin:openApiEnabled=true",
            "-P",
            "$ktorCompilerPlugin:openApiCodeInference=true",
            "-P",
            "$ktorCompilerPlugin:openApiOnlyCommented=false",
        )
        freeCompilerArgs.addAll(
            layout.buildDirectory.map { listOf("-P", "$ktorCompilerPlugin:openApiLogDir=${it.asFile.absolutePath}") },
        )
    }
}
