package dev.nilsschlegel

import dev.nilsschlegel.blog.adapters.persistence.ExposedBlogRepository
import dev.nilsschlegel.blog.adapters.web.blogRoutes
import dev.nilsschlegel.blog.application.BlogService
import io.github.nijasc.ktor.svelte.Svelte
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class Greeting(
    val message: String,
)

fun main() {
    val port = env("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(Svelte) {
        generateTs = true
        healthEndpoint = true
        transport {
            json()
            compression()
        }
    }
    configureErrors()
    configureDatabase()
    configureAuth()

    routing {
        get("/api/hello") {
            call.respond(Greeting("Hello from Ktor"))
        }
        databaseRoutes()
        authRoutes(DatabaseUserStore())
        blogRoutes(BlogService(ExposedBlogRepository()))
    }
}
